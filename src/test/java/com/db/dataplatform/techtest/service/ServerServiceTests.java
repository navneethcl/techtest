package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.component.impl.ServerImpl;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
import org.apache.logging.log4j.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import static com.db.dataplatform.techtest.TestDataHelper.TEST_NAME;
import static com.db.dataplatform.techtest.TestDataHelper.TEST_NAME_EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataEnvelopeApiObject;

@RunWith(MockitoJUnitRunner.class)
public class ServerServiceTests {

    @Mock
    private DataBodyService dataBodyServiceImplMock;

    @Mock
    private DataHeaderService dataHeaderServiceImplMock;

    private static final String ALGO_NAME = "MD5";

    private ModelMapper modelMapper;

    private DataEnvelope testDataEnvelope;

    private ServerImpl server;

    @Before
    public void setup() {
        ServerMapperConfiguration serverMapperConfiguration = new ServerMapperConfiguration();
        modelMapper = serverMapperConfiguration.createModelMapperBean();
        testDataEnvelope = createTestDataEnvelopeApiObject();

        server = new ServerImpl(dataBodyServiceImplMock,dataHeaderServiceImplMock, modelMapper);
        server.setAlgorithmName(ALGO_NAME);

    }

    @Test
    public void shouldSaveDataEnvelopeAsExpected() throws NoSuchAlgorithmException {
        boolean success = server.saveDataEnvelope(testDataEnvelope);
        assertThat(success).isTrue();
        verify(dataBodyServiceImplMock, times(1)).saveDataBody(any(DataBodyEntity.class));
    }

    @Test
    public void shouldGetDataAsExpected() {
        List<DataEnvelope> dataEnvelopeList = server.getDataEnvelopeByBlockType(BlockTypeEnum.BLOCKTYPEA);
        assertThat(dataEnvelopeList).isNotNull();
        verify(dataBodyServiceImplMock, times(1)).getDataByBlockType(any(BlockTypeEnum.class));
    }

    @Test
    public void shouldUpdateDataAsExpected() {
        when(server.updateBlockTypeByName(TEST_NAME,BlockTypeEnum.BLOCKTYPEA.name())).thenReturn(true);
        boolean isDataUpdated = server.updateBlockTypeByName(TEST_NAME, BlockTypeEnum.BLOCKTYPEA.name());
        assertThat(isDataUpdated).isTrue();
        verify(dataHeaderServiceImplMock, times(1)).updateDataByBlockName(any(String.class),any(BlockTypeEnum.class));
    }

    @Test
    public void shouldNotUpdateDataIfBlockNameEmpty() {
        boolean isDataUpdated = server.updateBlockTypeByName(TEST_NAME_EMPTY, BlockTypeEnum.BLOCKTYPEA.name());
        assertThat(isDataUpdated).isFalse();
    }

    @Test
    public void shouldNotUpdateDataIfBlockTypeEmpty() {
        boolean isDataUpdated = server.updateBlockTypeByName(TEST_NAME_EMPTY, Strings.EMPTY);
        assertThat(isDataUpdated).isFalse();
    }
}
