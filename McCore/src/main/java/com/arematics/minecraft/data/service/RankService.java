package com.arematics.minecraft.data.service;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.bukkit.Tablist;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.global.model.Rank;
import com.arematics.minecraft.data.global.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "rankCache", cacheManager = "globalCache")
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class RankService implements GlobalMessageReceiveService{

    private final RankRepository repository;
    private final RankPermissionService rankPermissionService;
    private final Server server;

    public List<Rank> findAll(){
        return repository.findAll();
    }

    @Cacheable(key = "#id")
    public Rank getById(long id){
        Optional<Rank> rank = repository.findById(id);
        if(!rank.isPresent()) throw new RuntimeException("Rank with id: " + id + " not exists");
        return rank.get();
    }

    public Rank findByName(String name){
        Optional<Rank> rank = repository.findByName(name);
        if(!rank.isPresent()) throw new RuntimeException("Rank with name: " + name + " not exists");
        return rank.get();
    }

    @CachePut(key = "#result.id")
    public Rank getDefaultRank(){
        Optional<Rank> rank = repository.findByName("User");
        return rank.orElseGet(() -> repository.save(new Rank(1L, "User", "U",
                "§b", false, "g", new Timestamp(System.currentTimeMillis()))));
    }

    public boolean hasPermission(Rank rank, String permission){
        return rankPermissionService.hasPermission(rank, permission);
    }

    @Override
    public String messageKey() {
        return "rank";
    }

    @Override
    public void onReceive(String data) {
        try{
            Rank rank = getById(Long.parseLong(data));
            server.onlineWithRank(rank.getId()).forEach(player -> ArematicsExecutor.runAsync(player::refreshCache));
            Tablist tablist = Boots.getBoot(CoreBoot.class).getContext().getBean(Tablist.class);
            tablist.patchTeam(rank);
        }catch (Exception ignore){}
    }
}
