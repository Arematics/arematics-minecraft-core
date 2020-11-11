package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.Ignored;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IgnoredRepository extends JpaRepository<Ignored, Long>, JpaSpecificationExecutor<Ignored> {
    List<Ignored> findAllByIgnorer(UUID ignorer);
    Optional<Ignored> findByIgnorerAndIgnored(UUID ignorer, UUID ignored);
    boolean existsByIgnorerAndIgnored(UUID ignorer, UUID ignored);
    void deleteByIgnorerAndIgnored(UUID ignorer, UUID ignored);
}
