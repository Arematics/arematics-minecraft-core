package com.arematics.minecraft.core.data.repository;

import com.arematics.minecraft.core.data.model.TestModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface TestRepository extends JpaRepository<TestModel, Integer> {
}
