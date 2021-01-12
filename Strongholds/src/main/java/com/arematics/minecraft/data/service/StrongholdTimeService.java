package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.ShTimeId;
import com.arematics.minecraft.data.mode.model.Stronghold;
import com.arematics.minecraft.data.mode.model.StrongholdTime;
import com.arematics.minecraft.data.mode.repository.StrongholdTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Service
public class StrongholdTimeService {

    private final StrongholdTimeRepository repository;

    @Autowired
    public StrongholdTimeService(StrongholdTimeRepository strongholdTimeRepository){
        this.repository = strongholdTimeRepository;
    }

    public StrongholdTime findById(ShTimeId shTimeId){
        Optional<StrongholdTime> time = repository.findById(shTimeId);
        if(!time.isPresent())
            throw new RuntimeException("Stronghold Time could not be found");
        return time.get();
    }

    public List<StrongholdTime> getTodayTimes(DayOfWeek dayOfWeek){
        return repository.findAllByDayOfWeek(dayOfWeek);
    }

    public List<StrongholdTime> getDayTimeMatching(DayOfWeek dayOfWeek, Time time){
        return repository.findAllByDayOfWeekAndTime(dayOfWeek, time);
    }

    public boolean isTimeUsed(DayOfWeek dayOfWeek, Time time){
        return repository.existsByDayOfWeekAndTime(dayOfWeek, time);
    }

    public void clearAllForStronghold(Stronghold stronghold){
        this.repository.deleteAllByStrongholdName(stronghold.getId());
    }

    public StrongholdTime update(StrongholdTime time){
        return this.repository.save(time);
    }
}
