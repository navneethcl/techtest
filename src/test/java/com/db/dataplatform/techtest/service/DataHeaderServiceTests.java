package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
import com.db.dataplatform.techtest.server.service.impl.DataHeaderServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataHeaderEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataHeaderServiceTests {

    @Mock
    private DataHeaderRepository dataHeaderRepositoryMock;

    private DataHeaderService dataHeaderService;
    private DataHeaderEntity expectedDataHeaderEntity;

    @Before
    public void setup() {
        expectedDataHeaderEntity = createTestDataHeaderEntity(Instant.now());

        dataHeaderService = new DataHeaderServiceImpl(dataHeaderRepositoryMock);
    }

    @Test
    public void shouldSaveDataHeaderEntityAsExpected(){
        dataHeaderService.saveHeader(expectedDataHeaderEntity);

        verify(dataHeaderRepositoryMock, times(1))
                .save(eq(expectedDataHeaderEntity));
    }

    @Test
    public void shouldUpdateDataHeaderEntityAsExpected(){
        when(dataHeaderRepositoryMock.updateByName(any(String.class),any(BlockTypeEnum.class))).thenReturn(1);
        Boolean isDataUpdated = dataHeaderService.updateDataByBlockName("blockname",BlockTypeEnum.BLOCKTYPEA);
        verify(dataHeaderRepositoryMock, times(1))
                .updateByName(any(String.class),eq(BlockTypeEnum.BLOCKTYPEA));
        assertThat(isDataUpdated).isTrue();
    }

    @Test
    public void shouldNotUpdateDataHeaderEntityAsExpected(){
        when(dataHeaderRepositoryMock.updateByName(any(String.class),any(BlockTypeEnum.class))).thenReturn(0);
        Boolean isDataUpdated = dataHeaderService.updateDataByBlockName("blockname",BlockTypeEnum.BLOCKTYPEA);
        verify(dataHeaderRepositoryMock, times(1))
                .updateByName(any(String.class),eq(BlockTypeEnum.BLOCKTYPEA));
        assertThat(isDataUpdated).isFalse();
    }

}
