package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.global.repository.UserRepository;
import com.arematics.minecraft.data.share.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Value("${mode.name}")
    private String modeName;

    private final UserRepository repository;
    private final RankService rankService;
    private final PermissionRepository permissionRepository;

    @Autowired
    public UserService(UserRepository userRepository, RankService rankService, PermissionRepository permissionRepository){
        this.repository = userRepository;
        this.rankService = rankService;
        this.permissionRepository = permissionRepository;
    }

    @Cacheable(cacheNames = "userCache")
    public User getUserByUUID(UUID uuid){
        Optional<User> user = repository.findById(uuid);
        if(!user.isPresent()) throw new RuntimeException("User with uuid: " + uuid + " could not be found");
        User entity = user.get();
        entity.getUserPermissions().addAll(permissionRepository
                .findAllByUserUUIDAndMode(entity.getUuid().toString(), modeName));
        return entity;
    }

    @CachePut(cacheNames = "userCache")
    public User createUser(UUID uuid){
        User user = new User(UUID.randomUUID(), uuid, new Timestamp(System.currentTimeMillis()), null, null,
                rankService.getDefaultRank(), null, new HashMap<>(), new HashSet<>());
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
