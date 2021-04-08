package com.arematics.minecraft.data.share.repository;

import com.arematics.minecraft.data.share.model.ItemCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemCollectionRepository extends JpaRepository<ItemCollection, String>, JpaSpecificationExecutor<ItemCollection> {

}
