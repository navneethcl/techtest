package com.db.dataplatform.techtest.server.component;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface Server {
    boolean saveDataEnvelope(DataEnvelope envelope) throws IOException, NoSuchAlgorithmException;
    List<DataEnvelope> getDataEnvelopeByBlockType(final BlockTypeEnum blockType);
    boolean updateBlockTypeByName(final String blockName,final String blockType);
    void pushDataToHadoopDataLake(final DataEnvelope dataEnvelope);
}
