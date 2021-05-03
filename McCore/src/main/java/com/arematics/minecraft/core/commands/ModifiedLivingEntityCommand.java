package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.ModifiedLivingEntity;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.world.InteractHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "world.interact.entities.modliving")
public class ModifiedLivingEntityCommand extends CoreCommand {

    public ModifiedLivingEntityCommand(){
        super("modentity");
        registerLongArgument("command");
    }

    @SubCommand("spawn {type} {command}")
    public boolean spawnModifiedEntity(CorePlayer sender, EntityType type, String command) {
        server.schedule().runSync(() -> {
            ModifiedLivingEntity modifiedLivingEntity = ModifiedLivingEntity.create(sender.getLocation(), type);
            modifiedLivingEntity.disableEntity();
            modifiedLivingEntity.setBindedCommand(command);
        });
        return true;
    }

    @SubCommand("setName {message}")
    public void setEntityName(CorePlayer sender, String message) {
        Entity nearestEntity = sender.handle(InteractHandler.class).next();
        if(nearestEntity == null) throw new CommandProcessException("No entity in range");
        nearestEntity.setCustomName(ChatColor.translateAlternateColorCodes('&', message));
        nearestEntity.setCustomNameVisible(true);
        sender.info("Entity name changed").handle();
    }
}
