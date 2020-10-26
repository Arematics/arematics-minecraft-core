package com.arematics.minecraft.core.entities;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.data.global.model.CommandEntity;
import com.arematics.minecraft.data.service.CommandEntityService;
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
    private String bindedCommand;

    public ModifiedLivingEntity(LivingEntity livingEntity){
        super(livingEntity);
        this.livingEntity = livingEntity;
    }

    public void disableEntity() {
        disableAI();
        disableKill();
        disableLootPickUP();
    }

    public String getBindedCommmand(){
        return hasBindedCommand() ? bindedCommand : null;
    }

    public boolean hasBindedCommand(){
        if(bindedCommand != null) return true;
        CommandEntityService service = Boots.getBoot(CoreBoot.class).getContext().getBean(CommandEntityService.class);
        try{
            CommandEntity entity = service.fetch(this.livingEntity.getUniqueId());
            this.bindedCommand = entity.getBindedCommand();
            return true;
        }catch (RuntimeException exception){
            exception.printStackTrace();
            return false;
        }
    }

    public void setBindedCommand(String bindedCommand){
        this.bindedCommand = bindedCommand;
        CommandEntityService service = Boots.getBoot(CoreBoot.class).getContext().getBean(CommandEntityService.class);
        CommandEntity entity = new CommandEntity(this.livingEntity.getUniqueId(), this.bindedCommand);
        service.add(entity);
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
