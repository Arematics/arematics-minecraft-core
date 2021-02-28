package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.Kit;
import com.arematics.minecraft.data.mode.repository.KitRepository;
import com.arematics.minecraft.data.share.model.Cooldown;
import com.arematics.minecraft.data.share.model.CooldownKey;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "kitCache")
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

    @Cacheable(key = "#name")
    public Kit findKit(String name){
        Optional<Kit> result = repository.findByName(name);
        if(!result.isPresent()) throw new RuntimeException("Kit with name: " + name + " could not be found");
        return result.get();
    }

    @CachePut(key = "#result.name")
    public Kit update(Kit kit){
        return repository.save(kit);
    }

    @CacheEvict(key = "#kit.name")
    public void delete(Kit kit){
        repository.delete(kit);
    }

    public boolean isPermitted(Player player, Kit kit){
        return kit.getPermission() == null || userService.hasPermission(player.getUniqueId(), kit.getPermission());
    }

    public boolean hasCooldownOnKit(UUID uuid, Kit kit){
        CooldownKey key = from(uuid, kit);
        return cooldownService.hasCooldown(key);
    }

    public Optional<Cooldown> getCooldownOnKit(UUID uuid, Kit kit){
        CooldownKey key = from(uuid, kit);
        return cooldownService.getModeCooldown(key);
    }

    public void setCooldownOnKit(UUID uuid, Kit kit){
        Cooldown cooldown = new Cooldown(uuid.toString(), kit.getName(), System.currentTimeMillis() + kit.getCooldown());
        cooldownService.ofMode(cooldown);
    }

    public List<String> findKitNames(){
        return repository.findNames();
    }

    private CooldownKey from(UUID uuid, Kit kit){
        return new CooldownKey(uuid.toString(), kit.getName());
    }
}
