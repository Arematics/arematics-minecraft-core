package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.ItemPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemPriceRepository extends JpaRepository<ItemPrice, String>, JpaSpecificationExecutor<ItemPrice> {

}
