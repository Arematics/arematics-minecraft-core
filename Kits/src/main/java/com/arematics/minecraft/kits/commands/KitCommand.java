package com.arematics.minecraft.kits.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.data.mode.model.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KitCommand extends CoreCommand {

    public KitCommand() {
        super("kit", "kits");
    }

    @Override
    public boolean onDefaultExecute(CommandSender sender){
        Messages.create("kit_list")
                .to(sender)
                .DEFAULT()
                .replace("cmd_usage", "\n/kits\n/kit {kit}")
                .handle();
        return true;
    }

    @SubCommand("{kit}")
    public boolean giveKit(Player player, Kit kit) {
        UUID uuid = player.getUniqueId();
        return true;
    }
}
