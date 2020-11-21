package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.CorePlayer;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class TpaDenyCommand extends CoreCommand {
    private final TpaCommand tpaCommand;

    @Autowired
    public TpaDenyCommand(TpaCommand tpaCommand) {
        super("tpadeny");
        this.tpaCommand = tpaCommand;
    }

    @Override
    protected boolean onDefaultCLI(CommandSender sender) {
        CorePlayer receiver = CorePlayer.get((Player) sender);
        getTpaCommand().denyTpa(receiver);
        return true;
    }

}
