package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.entities.ModifiedLivingEntity;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import org.bukkit.entity.EntityType;
import org.springframework.stereotype.Component;

@Component
public class ModifiedLivingEntityCommand extends CoreCommand {

    public ModifiedLivingEntityCommand(){
        super("modentity");
        registerLongArgument("command");
    }

    @SubCommand("spawn {type} {command}")
    public boolean spawnModifiedEntity(CorePlayer sender, EntityType type, String command) {
        ArematicsExecutor.syncRun(() -> {
            ModifiedLivingEntity modifiedLivingEntity = ModifiedLivingEntity.create(sender.getLocation(), type);
            modifiedLivingEntity.disableEntity();
            modifiedLivingEntity.setBindedCommand(command);
        });
        return true;
    }
}
