package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.data.global.model.BukkitItemMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PlayerBuffId.class)
@Table(name = "buff")
public class PlayerBuff implements Serializable, BukkitItemMapper {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;
    @Id
    private String potionEffectType;
    private byte strength;
    private Timestamp endTime;
    private boolean active;

    @Override
    public CoreItem mapToItem(Server server) {
        String subCommand = this.isActive() ? "deActivate" : "activate";
        String active = this.isActive() ? "§aYes" : "§cNo";
        String perm = this.getEndTime() == null ? "§aYes" : "§cNo";
        String endTime = this.getEndTime() == null ? "§cNever" :
                "§a" + TimeUtils.fetchEndDate(this.getEndTime());
        return server.generateNoModifier(Material.POTION)
                .bindCommand("buffs " + subCommand + " " + this.getPotionEffectType())
                .setName("§cBuff: " + this.getPotionEffectType())
                .addToLore("§8Strength: " + (this.getStrength() + 1))
                .addToLore("§8Active: " + active)
                .addToLore("§8Permanent: " + perm)
                .addToLore("§8Ending: " + endTime);
    }
}
