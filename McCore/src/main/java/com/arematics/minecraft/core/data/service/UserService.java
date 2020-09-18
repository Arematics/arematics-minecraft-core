package com.arematics.minecraft.core.data.service;

import com.arematics.minecraft.core.data.model.User;
import com.arematics.minecraft.core.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository repository;
    private final RankService rankService;

    @Autowired
    public UserService(UserRepository userRepository, RankService rankService){
        this.repository = userRepository;
        this.rankService = rankService;
    }

    @Cacheable(cacheNames = "userCache")
    public User getUserByUUID(UUID uuid){
        Optional<User> user = repository.findById(uuid);
        if(!user.isPresent()) throw new RuntimeException("User with uuid: " + uuid + " could not be found");
        return user.get();
    }

    @CachePut(cacheNames = "userCache")
    public User createUser(UUID uuid){
        User user = new User(UUID.randomUUID(), uuid, new Timestamp(System.currentTimeMillis()), null, null,
                rankService.getDefaultRank(), null, new HashSet<>());
        return repository.save(user);
    }

    @CachePut(cacheNames = "userCache")
    public User update(User user){
        return repository.save(user);
    }

    public User getOrCreateUser(UUID uuid){
        try{
            return getUserByUUID(uuid);
        }catch (RuntimeException exception){
            return createUser(uuid);
        }
    }
}
