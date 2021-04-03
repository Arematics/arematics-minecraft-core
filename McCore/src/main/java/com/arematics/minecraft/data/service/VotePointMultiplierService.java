package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.VotePointMultiplier;
import com.arematics.minecraft.data.global.repository.VotePointMultiplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class VotePointMultiplierService {
    private final VotePointMultiplierRepository votePointMultiplierRepository;

    public VotePointMultiplier findNearest(int streak){
        Optional<VotePointMultiplier> multiplier = votePointMultiplierRepository.findVotePointMultiplierByStreakIsLessThan(streak);
        if(!multiplier.isPresent())
            throw new RuntimeException("Could not find nearest vote streak");
        return multiplier.get();
    }
}
