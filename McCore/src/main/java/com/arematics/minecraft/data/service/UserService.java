package com.arematics.minecraft.data.service;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.global.repository.UserRepository;
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
    private final UserPermissionService userPermissionService;

    @Autowired
    public UserService(UserRepository userRepository,
                       RankService rankService,
                       UserPermissionService userPermissionService){
        this.repository = userRepository;
        this.rankService = rankService;
        this.userPermissionService = userPermissionService;
    }

    @Cacheable(cacheNames = "userCache", key = "#uuid")
    public User getUserByUUID(UUID uuid){
        Optional<User> user = repository.findById(uuid);
        if(!user.isPresent()) throw new RuntimeException("User with uuid: " + uuid + " could not be found");
        return user.get();
    }

    public User findByName(String name){
        Optional<User> user = repository.findByLastName(name);
        if(!user.isPresent()) throw new RuntimeException("User with lastName: " + name + " could not be found");
        return user.get();
    }

    @CachePut(cacheNames = "userCache", key = "#result.uuid")
    public User createUser(UUID uuid, String name){
        User user = new User(UUID.randomUUID(), uuid, name, new Timestamp(System.currentTimeMillis()), null,
                null, rankService.findByName("User"), null, 0, new HashMap<>(), new HashSet<>());
        return repository.save(user);
    }

    @CachePut(cacheNames = "userCache", key = "#result.uuid")
    public User update(User user){
        return repository.save(user);
    }

    public User getOrCreateUser(UUID uuid, String name){
        try{
            return getUserByUUID(uuid);
        }catch (RuntimeException exception){
            return createUser(uuid, name);
        }
    }

    public boolean hasPermission(UUID uuid, String permission){
        User user = getUserByUUID(uuid);
        return rankService.hasPermission(user.getRank(), permission) ||
                userPermissionService.hasPermission(uuid, permission);
    }

    public User getOrCreateUser(CorePlayer player){
        try{
            return getUserByUUID(player.getUUID());
        }catch (RuntimeException exception){
            return createUser(player.getUUID(), player.getPlayer().getName());
        }
    }
}
