package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.entities.ModifiedLivingEntity;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModifiedLivingEntityCommand extends CoreCommand {

    public ModifiedLivingEntityCommand(){
        super("modentity");
        registerLongArgument("command");
    }

    @Override
    public boolean onDefaultExecute(CommandSender sender){
        List<String> subCommands = super.getSubCommands();
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .eachReplace("cmd_usage", subCommands.toArray(new String[]{}))
                .setHover(HoverAction.SHOW_TEXT, "Open to chat")
                .setClick(ClickAction.SUGGEST_COMMAND, "/modentity %value%")
                .END()
                .handle();
        return true;
    }

    @SubCommand("spawn {type} {command}")
    public boolean spawnModifiedEntity(Player sender, EntityType type, String command) {
        ModifiedLivingEntity modifiedLivingEntity = ModifiedLivingEntity.create(sender.getLocation(), type);
        modifiedLivingEntity.disableEntity();
        modifiedLivingEntity.setBindedCommand(command);
        return true;
    }
}
