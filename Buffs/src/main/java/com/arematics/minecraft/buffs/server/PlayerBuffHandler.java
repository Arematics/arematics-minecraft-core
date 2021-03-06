package com.arematics.minecraft.buffs.server;

import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.mode.model.PlayerBuff;
import com.arematics.minecraft.data.service.PlayerBuffService;
import lombok.RequiredArgsConstructor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class PlayerBuffHandler {
    public static PotionEffectType[] POSITIVE_BUFFS = new PotionEffectType[]{
            PotionEffectType.SPEED,
            PotionEffectType.FAST_DIGGING,
            PotionEffectType.INCREASE_DAMAGE,
            PotionEffectType.HEAL,
            PotionEffectType.HARM,
            PotionEffectType.JUMP,
            PotionEffectType.REGENERATION,
            PotionEffectType.DAMAGE_RESISTANCE,
            PotionEffectType.FIRE_RESISTANCE,
            PotionEffectType.WATER_BREATHING,
            PotionEffectType.INVISIBILITY,
            PotionEffectType.NIGHT_VISION,
            PotionEffectType.HEALTH_BOOST,
            PotionEffectType.ABSORPTION,
            PotionEffectType.SATURATION
    };

    private final Server server;
    private final PlayerBuffService playerBuffService;

    public PlayerBuffService getPlayerBuffService(){
        return playerBuffService;
    }

    public void enableBuffs(CorePlayer player){
        List<PlayerBuff> activeBuffs = playerBuffService.findAllActiveBuffsByPlayer(player.getUUID());
        activeBuffs.forEach((buff) -> addPotionEffectToPlayer(player, buff));
    }

    public void addNewBuff(UUID uuid, PlayerBuff buff){
        playerBuffService.update(buff);
        CorePlayer online = server.findOnline(uuid);
        if(online != null) addPotionEffectToPlayer(online, buff);
    }

    public void removeBuff(UUID uuid, PlayerBuff buff){
        playerBuffService.delete(buff);
        CorePlayer online = server.findOnline(uuid);
        if(online != null) removePotionEffectFromPlayer(online, PotionEffectType.getByName(buff.getPotionEffectType()));
    }

    public void addPotionEffectToPlayer(CorePlayer player, PlayerBuff buff){
        removePotionEffectFromPlayer(player, PotionEffectType.getByName(buff.getPotionEffectType()));
        ArematicsExecutor.syncRun(() -> player.getPlayer().addPotionEffect(generatePotionEffect(buff)));
    }

    public void removePotionEffectFromPlayer(CorePlayer player, PotionEffectType potionEffectType){
        if(player.getPlayer().hasPotionEffect(potionEffectType))
            ArematicsExecutor.syncRun(() -> player.getPlayer().removePotionEffect(potionEffectType));
    }

    private PotionEffect generatePotionEffect(PlayerBuff buff){
        return new PotionEffect(PotionEffectType.getByName(buff.getPotionEffectType()), calculateDuration(buff), buff.getStrength());
    }

    private int calculateDuration(PlayerBuff buff){
        if(buff.getEndTime() == null) return Integer.MAX_VALUE;
        long seconds = Duration.between(LocalDateTime.now(), buff.getEndTime().toLocalDateTime()).getSeconds();
        if(seconds > 32*60*60) return Integer.MAX_VALUE;
        return (int) TimeUtils.toTicks(seconds, TimeUnit.SECONDS);
    }
}
