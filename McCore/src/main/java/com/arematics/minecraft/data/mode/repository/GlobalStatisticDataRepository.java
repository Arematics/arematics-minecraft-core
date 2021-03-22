package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.GlobalStatisticData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GlobalStatisticDataRepository extends JpaRepository<GlobalStatisticData, String>,
        JpaSpecificationExecutor<GlobalStatisticData> {

}
