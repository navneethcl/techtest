package com.db.dataplatform.techtest.client.component.impl;

import com.db.dataplatform.techtest.client.api.model.DataEnvelope;
import com.db.dataplatform.techtest.client.component.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Client code does not require any test coverage
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientImpl implements Client {

    public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
    public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
    public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void pushData(DataEnvelope dataEnvelope) {
        log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);
        restTemplate.postForEntity(URI_PUSHDATA,dataEnvelope, Boolean.class);
    }

    @Override
    public List<DataEnvelope> getData(String blockTypeName) {
        log.info("Query for data with header block type: " +  blockTypeName);
        final Map<String,String> uriVariables=new HashMap<>();
        uriVariables.put("blockType", blockTypeName);
        final URI uri = URI_GETDATA.expand(uriVariables);
        return restTemplate.getForObject(uri, List.class);
    }

    @Override
    public boolean updateData(String blockName, String newBlockType) {
        log.info("Updating blocktype to {} for block with name {}", newBlockType, blockName);
        final Map<String,String> uriVariables=new HashMap<>();
        uriVariables.put("newBlockType", newBlockType);
        uriVariables.put("name", blockName);
        final URI uri = URI_PATCHDATA.expand(uriVariables);
        return restTemplate.patchForObject(uri,null,Boolean.class);
    }


}
