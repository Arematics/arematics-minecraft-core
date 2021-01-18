package com.arematics.minecraft.data.mode.repository;

import com.arematics.minecraft.data.mode.model.ShTimeId;
import com.arematics.minecraft.data.mode.model.StrongholdTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.List;

public interface StrongholdTimeRepository extends JpaRepository<StrongholdTime, ShTimeId> {
    List<StrongholdTime> findAllByDayOfWeekOrderByTimeAsc(DayOfWeek dayOfWeek);
    void deleteAllByStrongholdName(String name);
    boolean existsByDayOfWeekAndTime(DayOfWeek dayOfWeek, Time time);
    List<StrongholdTime> findAllByDayOfWeekAndTime(DayOfWeek dayOfWeek, Time time);
}
