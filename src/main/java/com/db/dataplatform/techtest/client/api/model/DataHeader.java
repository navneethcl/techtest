package com.db.dataplatform.techtest.client.api.model;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@JsonSerialize(as = DataHeader.class)
@JsonDeserialize(as = DataHeader.class)
@Getter
@AllArgsConstructor
public class DataHeader {

    @NotNull
    public final String name;

    @NotNull
    private final BlockTypeEnum blockType;

    @NotNull
    private String checksum;

}
