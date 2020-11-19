package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.permissions.Permissions;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.mode.model.Warp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class WarpCommand extends CoreCommand {

    private static HashMap<String, Warp> warps = new HashMap<>();

    public static HashMap<String, Warp> getWarps() {
        return warps;
    }

    public WarpCommand() { super("warp"); }

    @SubCommand("{warp}")
    public void warpTo(CorePlayer player, Warp warp) {
        ArematicsExecutor.syncRun(() -> player.getPlayer().teleport(warp.getLocation()));
    }

    @SubCommand("set {warp}")
    public void setWarpTo(CorePlayer player, String warpName) {

        Permissions.check(player.getPlayer(), "set-warp").ifPermitted(sender -> setWarp(sender, warpName)).submit();

    }

    private static void setWarp(CommandSender sender, String warpName) {
        CorePlayer player = CorePlayer.get((Player) sender);

        if(warps.containsKey(warpName)) {
            Warp warp = warps.get(warpName);
            warp.setLocation(player.getLocation());
            warps.replace(warpName,warp);
        } else {
            Warp warp = new Warp(warpName, player.getLocation());
            warps.put(warpName, warp);
        }

        player.info("Warp has been set to your current location").handle();

    }
}


