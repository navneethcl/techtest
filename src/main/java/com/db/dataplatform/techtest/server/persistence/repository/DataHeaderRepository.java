package com.db.dataplatform.techtest.server.persistence.repository;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface DataHeaderRepository extends JpaRepository<DataHeaderEntity, Long> {

     @Transactional
     @Modifying
     @Query(value = "Update DataHeaderEntity as dhe set dhe.blocktype =:blockType  where  dhe.name =:name")
     public int updateByName(@Param("name") String name, @Param("blockType") BlockTypeEnum blockType);

}
