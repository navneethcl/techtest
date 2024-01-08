package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

    public static final String URI_PUSH_BIGDATA = "http://localhost:8090/hadoopserver/pushbigdata";

    @Autowired
    private final DataBodyService dataBodyServiceImpl;
    @Autowired
    private final DataHeaderService dataHeaderServiceImpl;

    private final ModelMapper modelMapper;

    @Setter
    @Value("${checksum.algorithm.name:MD5}")
    private String algorithmName;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * @param envelope message
     * @return true if there is a match with the client provided checksum.
     */
    @Override
    public boolean saveDataEnvelope(final DataEnvelope envelope) throws NoSuchAlgorithmException {
        // Match checksum provided by client and generated checksum
        if(!getChecksum(envelope).equalsIgnoreCase(envelope.getDataHeader().getChecksum()))
        {
           return false;
        }
        // Save envelop body data to persistence.
        persist(envelope);

        log.info("Data persisted successfully, data name: {}", envelope.getDataHeader().getName());
        return true;
    }

    @Override
    public List<DataEnvelope> getDataEnvelopeByBlockType(final BlockTypeEnum blockType) {

        final  List<DataBodyEntity> dataBodyEntities =  dataBodyServiceImpl.getDataByBlockType(blockType);
        final List<DataEnvelope> dataEnvelopeList = new ArrayList<>();
        constructDataEnvelop(dataBodyEntities, dataEnvelopeList);
        return dataEnvelopeList;
    }

    @Override
    public boolean updateBlockTypeByName(final String blockName, final String blockType) {
        if(!StringUtils.isEmpty(blockName) && !StringUtils.isEmpty(blockType)) {
            return dataHeaderServiceImpl.updateDataByBlockName(blockName, BlockTypeEnum.valueOf(blockType));
        }
        return false;
    }

    @Override
    @Retryable(
            include = {HttpServerErrorException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000))
    public void pushDataToHadoopDataLake(final DataEnvelope dataEnvelope)
    {
        restTemplate.postForEntity(URI_PUSH_BIGDATA,dataEnvelope.getDataBody().getDataBody(), HttpStatus.class);
    }

    @Recover
    public void recover() {
        log.error("all attempts retried to sent data to hadoop");
    }

    private List<DataEnvelope> constructDataEnvelop(List<DataBodyEntity> dataBodyEntities, List<DataEnvelope> dataEnvelopeList) {
        //we can do better this code by replacing with convertor and use model wrapper
        for (DataBodyEntity dbe: dataBodyEntities) {
            DataHeader dh = new DataHeader(dbe.getDataHeaderEntity().getName(),dbe.getDataHeaderEntity().getBlocktype(),dbe.getDataHeaderEntity().getDataChecksum());
            DataEnvelope de= new DataEnvelope(dh, new DataBody(dbe.getDataBody()));
            dataEnvelopeList.add(de);
        }
        return dataEnvelopeList;

    }

    private void persist(final DataEnvelope envelope) {
        log.info("Persisting data with attribute name: {}", envelope.getDataHeader().getName());
        DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);
        DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);

        saveData(dataBodyEntity);
    }

    private void saveData(DataBodyEntity dataBodyEntity) {
        dataBodyServiceImpl.saveDataBody(dataBodyEntity);
    }

    /**
     * calculate and return checksum
     * @param envelope envelope to get databody
     * @return checksum
     * @throws NoSuchAlgorithmException
     */
    private String getChecksum(final DataEnvelope envelope) throws NoSuchAlgorithmException {
        //calculate checksum
        final MessageDigest messageDigest = MessageDigest.getInstance(algorithmName);
        final byte[] bytesOfMessage = envelope.getDataBody().getDataBody().getBytes(StandardCharsets.UTF_8);
        final byte[] hash = messageDigest.digest(bytesOfMessage);
        return new BigInteger(1, hash).toString(16);
    }

}
