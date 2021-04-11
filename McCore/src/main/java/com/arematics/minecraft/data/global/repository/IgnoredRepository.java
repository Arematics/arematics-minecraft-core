package com.arematics.minecraft.data.global.repository;

import com.arematics.minecraft.data.global.model.Ignored;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IgnoredRepository extends JpaRepository<Ignored, Long>, JpaSpecificationExecutor<Ignored> {
    Page<Ignored> findAllByIgnorer(UUID ignorer, Pageable pageable);
    Set<Ignored> findAllByIgnorer(UUID ignorer);
    Optional<Ignored> findByIgnorerAndIgnored(UUID ignorer, UUID ignored);
    boolean existsByIgnorerAndIgnored(UUID ignorer, UUID ignored);
    void deleteByIgnorerAndIgnored(UUID ignorer, UUID ignored);
}
