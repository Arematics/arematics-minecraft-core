package com.arematics.minecraft.data.service;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.global.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "userCache", cacheManager = "globalCache")
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class UserService implements GlobalMessageReceiveService {

    private final UserRepository userRepository;
    private final RankService rankService;
    private final UserPermissionService userPermissionService;

    @Cacheable(key = "#uuid")
    public User getUserByUUID(UUID uuid){
        Optional<User> user = userRepository.findById(uuid);
        if(!user.isPresent()) throw new RuntimeException("User with uuid: " + uuid + " could not be found");
        return user.get();
    }

    public User findByName(String name){
        Optional<User> user = userRepository.findByLastName(name);
        if(!user.isPresent()) throw new RuntimeException("User with lastName: " + name + " could not be found");
        return user.get();
    }

    @CachePut(key = "#result.uuid")
    public User createUser(UUID uuid, String name){
        User user = new User(UUID.randomUUID(), uuid, name, new Timestamp(System.currentTimeMillis()),
                rankService.getDefaultRank(), null, 0, "", new HashMap<>());
        return userRepository.save(user);
    }

    @CachePut(key = "#result.uuid")
    public User update(User user){
        return userRepository.save(user);
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

    @Override
    public String messageKey() {
        return "user";
    }

    @Override
    public void onReceive(final String data) {
        try{
            Server server = Boots.getBoot(CoreBoot.class).getContext().getBean(Server.class);
            UUID uuid = UUID.fromString(data);
            User user = getUserByUUID(uuid);
            Optional<CorePlayer> online = user.online(server);
            online.ifPresent(CorePlayer::refreshCache);
        }catch (Exception ignore){}
    }
}
