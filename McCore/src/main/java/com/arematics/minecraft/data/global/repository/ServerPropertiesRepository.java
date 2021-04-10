package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.ServerProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServerPropertiesRepository extends JpaRepository<ServerProperties, String>, JpaSpecificationExecutor<ServerProperties> {

}
