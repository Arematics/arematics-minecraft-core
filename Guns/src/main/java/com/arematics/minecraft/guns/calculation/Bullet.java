package com.arematics.minecraft.guns.calculation;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.Weapon;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class Bullet {
    private static Map<UUID, Bullet> activeBullets = new HashMap<>();

    public static Bullet findBulletById(UUID uuid){
        return Bullet.activeBullets.get(uuid);
    }

    public static void remove(UUID uuid){
        Bullet.activeBullets.remove(uuid);
    }

    public static void register(Bullet bullet){
        Bullet.activeBullets.put(bullet.getBulletId(), bullet);
    }

    private final UUID bulletId;
    private final CorePlayer shooter;
    private final Weapon firegun;
    private final int damage;
}
