package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.mode.model.Warp;
import com.arematics.minecraft.data.service.WarpService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "spawn", description = "teleports to spawn")
public class SpawnCommand extends CoreCommand {

    private final WarpCommand warpCommand;
    private final WarpService warpService;


    @Autowired
    public SpawnCommand(WarpCommand warpCommand, WarpService warpService) {
        super("spawn");
        this.warpCommand = warpCommand;
        this.warpService = warpService;
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {
        if (!(sender instanceof Player))
            throw new CommandProcessException("Only Player can perform this command");
        CorePlayer player = CorePlayer.get((Player) sender);

        try {
            Warp warp = warpService.getWarp("spawn");
            this.warpCommand.warpTo(player, warp);
        } catch (Exception e) {
            player.warn("Warp was not set yet").handle();
        }

    }
}
