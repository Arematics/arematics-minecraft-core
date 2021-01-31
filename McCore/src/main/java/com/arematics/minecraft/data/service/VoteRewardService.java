package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.VoteReward;
import com.arematics.minecraft.data.mode.repository.VoteRewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoteRewardService {

    private final VoteRewardRepository repository;

    @Autowired
    public VoteRewardService(VoteRewardRepository voteRewardRepository){
        this.repository = voteRewardRepository;
    }

    @Cacheable(cacheNames = "vote_rewards", key = "#id")
    public VoteReward findVoteReward(String id){
        Optional<VoteReward> reward = repository.findById(id);
        if(!reward.isPresent())
            throw new RuntimeException("Vote Reward with id: " + id + " could not be found");
        return reward.get();
    }

    public List<VoteReward> findAll(){
        return repository.findAllByOrderByCosts();
    }

    @CachePut(cacheNames = "vote_rewards", key = "#result.id")
    public VoteReward update(VoteReward voteReward){
        return repository.save(voteReward);
    }

    @CacheEvict(cacheNames = "vote_rewards", key = "#voteReward.id")
    public void deleteReward(VoteReward voteReward){
        repository.delete(voteReward);
    }
}
