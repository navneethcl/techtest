package com.db.dataplatform.techtest.server.service.impl;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataHeaderRepository;
import com.db.dataplatform.techtest.server.service.DataHeaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataHeaderServiceImpl implements DataHeaderService {

    private final DataHeaderRepository dataHeaderRepository;

    @Override
    public void saveHeader(DataHeaderEntity entity) {
        dataHeaderRepository.save(entity);
    }

    @Override
    public boolean updateDataByBlockName(String blockName, BlockTypeEnum blockType) {
        final int isDataUpdated = dataHeaderRepository.updateByName(blockName, blockType);
        return isDataUpdated == 1;
    }
}
