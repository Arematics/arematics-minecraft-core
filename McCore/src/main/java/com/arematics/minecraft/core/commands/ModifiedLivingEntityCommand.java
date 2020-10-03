package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.PluginCommand;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.entities.ModifiedLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@PluginCommand(aliases = {})
public class ModifiedLivingEntityCommand extends CoreCommand {

    public ModifiedLivingEntityCommand(){
        super("modentity");
    }

    @SubCommand("spawn {type}")
    public boolean spawnModifiedEntity(Player sender, EntityType type) {
        ModifiedLivingEntity modifiedLivingEntity = ModifiedLivingEntity.create(sender.getLocation(), type);
        modifiedLivingEntity.disableEntity();
        modifiedLivingEntity.setBindedCommand("sound list");
        return true;
    }
}
