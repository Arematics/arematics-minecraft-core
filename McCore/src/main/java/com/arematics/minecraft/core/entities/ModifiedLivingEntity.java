package com.arematics.minecraft.core.entities;

import de.tr7zw.nbtapi.NBTEntity;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

@Getter
public class ModifiedLivingEntity extends NBTEntity {

    public static final String CLICK_INTERACT = "clickInteract";

    public static ModifiedLivingEntity create(Location location, EntityType type){
        return new ModifiedLivingEntity((LivingEntity) location.getWorld().spawnEntity(location, type));
    }

    private final LivingEntity livingEntity;

    public ModifiedLivingEntity(LivingEntity livingEntity){
        super(livingEntity);
        this.livingEntity = livingEntity;
    }

    public void disableEntity() {
        disableAI();
        disableKill();
        disableLootPickUP();
    }

    public void disableAI(){
        Location entityLocation = livingEntity.getLocation();
        entityLocation.setYaw(0);
        entityLocation.setPitch(0);
        livingEntity.teleport(entityLocation);
        this.setInteger("NoAI", 1);
    }

    public void setInvulnerable(boolean invulnerable){
        this.setBoolean("Invulnerable", invulnerable);
    }

    public void disableKill(){
        setInvulnerable(true);
    }

    public void disableLootPickUP(){
        this.setBoolean("CanPickUpLoot", false);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
