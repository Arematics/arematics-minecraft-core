package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.CurrencyData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyDataRepository extends JpaRepository<CurrencyData, Long> {
}
