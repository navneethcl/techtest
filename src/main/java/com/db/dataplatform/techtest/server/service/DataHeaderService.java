package com.db.dataplatform.techtest.server.service;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;

public interface DataHeaderService {
    void saveHeader(DataHeaderEntity entity);
    boolean updateDataByBlockName(String blockName, BlockTypeEnum blockType);
}
