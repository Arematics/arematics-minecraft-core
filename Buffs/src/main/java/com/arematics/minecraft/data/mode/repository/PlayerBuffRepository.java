package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.PlayerBuff;
import com.arematics.minecraft.data.mode.model.PlayerBuffId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerBuffRepository extends PagingAndSortingRepository<PlayerBuff, PlayerBuffId> {
    @Query("select buff from PlayerBuff buff WHERE " +
            "buff.id = :uuid " +
            "AND buff.potionEffectType = :potionEffectType " +
            "AND (buff.endTime >= :timestamp OR buff.endTime is null)")
    Optional<PlayerBuff> findByValidBuff(@Param("uuid") UUID uuid,
                                         @Param("potionEffectType") String potionEffectType,
                                         @Param("timestamp") Timestamp timestamp);
    List<PlayerBuff> findAllById(UUID uuid);

    /**
     * List all active player buffs with end time in future or no end time
     * @param uuid Player UUID
     * @param timestamp Time now
     * @return Page with PlayerBuffs
     */
    List<PlayerBuff> findAllByIdAndActiveIsTrueAndEndTimeIsAfterOrEndTimeIsNullAndActiveIsTrue(UUID uuid,
                                                                                               Timestamp timestamp);

    /**
     * List all player buffs with end time in future or no end time
     * @param uuid Player UUID
     * @param active If buffs should be active or inactive
     * @param timestamp Time now
     * @param pageable Pageable
     * @return Page with PlayerBuffs
     */
    @Query("select buff from PlayerBuff buff WHERE " +
            "buff.id = :uuid " +
            "AND buff.active = :active " +
            "AND (buff.endTime >= :timestamp OR buff.endTime is null)")
    Page<PlayerBuff> findAllByActive(@Param("uuid") UUID uuid,
                                     @Param("active") boolean active,
                                     @Param("timestamp") Timestamp timestamp,
                                     Pageable pageable);

    /**
     * List all player buffs with ending
     * @param uuid Player UUID
     * @param timestamp Time now
     * @param pageable Pageable
     * @return Page with PlayerBuffs
     */
    Page<PlayerBuff> findAllByIdAndEndTimeIsAfter(UUID uuid,
                                                  Timestamp timestamp,
                                                  Pageable pageable);

    /**
     * List all valid buffs
     * @param uuid Player UUID
     * @param pageable Pageable
     * @return Page with PlayerBuffs
     */
    Page<PlayerBuff> findAllByIdAndEndTimeIsAfterOrEndTimeIsNull(UUID uuid,
                                                                 Timestamp timestamp,
                                                                 Pageable pageable);

    /**
     * List all permanent buffs
     * @param uuid Player UUID
     * @param pageable Pageable
     * @return Page with PlayerBuffs
     */
    Page<PlayerBuff> findAllByIdAndEndTimeIsNull(UUID uuid,
                                                 Pageable pageable);
}
