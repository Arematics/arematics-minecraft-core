package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.ItemPrice;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ItemPriceRepository extends PagingAndSortingRepository<ItemPrice, String>, JpaSpecificationExecutor<ItemPrice> {
}
