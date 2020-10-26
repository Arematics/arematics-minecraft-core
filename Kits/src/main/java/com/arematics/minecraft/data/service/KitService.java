package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.mode.model.Kit;
import com.arematics.minecraft.data.mode.repository.KitRepository;
import com.arematics.minecraft.data.share.model.CooldownKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class KitService {

    private final KitRepository repository;
    private final UserService userService;
    private final CooldownService cooldownService;

    @Autowired
    public KitService(KitRepository repository, UserService userService, CooldownService cooldownService){
        this.repository = repository;
        this.userService = userService;
        this.cooldownService = cooldownService;
    }

    private Kit findKit(long id){
        Optional<Kit> result = repository.findById(id);
        if(!result.isPresent()) throw new RuntimeException("Kit with id: " + id + " could not be found");
        return result.get();
    }

    @Cacheable(cacheNames = "kitCache", key = "#name")
    public Kit findKit(String name){
        try{
            return findKit(Integer.parseInt(name));
        }catch (NumberFormatException nfe){
            Optional<Kit> result = repository.findByName(name);
            if(!result.isPresent()) throw new RuntimeException("Kit with name: " + name + " could not be found");
            return result.get();
        }
    }

    @CachePut(cacheNames = "kitCache", key = "#kit.name")
    public void update(Kit kit){
        repository.save(kit);
    }

    public boolean isPermitted(UUID uuid, Kit kit){
        User user = userService.getUserByUUID(uuid);
        return user.getUserPermissions().contains(kit.getPermission());
    }

    public boolean hasCooldownOnKit(UUID uuid, Kit kit){
        CooldownKey key = from(uuid, kit);
        return cooldownService.hasCooldown(key);
    }

    private CooldownKey from(UUID uuid, Kit kit){
        return new CooldownKey(uuid.toString(), kit.getName());
    }
}