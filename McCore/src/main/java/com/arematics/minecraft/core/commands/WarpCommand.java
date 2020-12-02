package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.mode.model.Warp;
import com.arematics.minecraft.data.service.WarpService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
@Perm(permission = "warp", description = "warp to spot")
public class WarpCommand extends CoreCommand {

    private final WarpService warpService;

    @Autowired
    public WarpCommand(WarpService warpService) { super("warp"); this.warpService = warpService; }

    @SubCommand("{warp}")
    @Perm(permission = "to", description = "set Warp")
    public void warpTo(CorePlayer player, Warp warp) {
        teleport(player, warp, false);
        player.info("You were teleported to warp " + warp.getName()).handle();
    }

    public void teleport(CorePlayer player, Warp warp, boolean instant){
        if(!instant) player.teleport(warp.getLocation());
        else player.instantTeleport(warp.getLocation());
    }

    @SubCommand("set {warp}")
    @Perm(permission = "set", description = "set Warp")
    public void setWarpTo(CorePlayer player, String warpName) {
        setWarp(player, warpName);
    }

    @SubCommand("del {warp}")
    @Perm(permission = "del", description = "del Warp")
    public void delWarp(CorePlayer player, String warpName) {
        delWarpExecute(player, warpName);
    }

    private void delWarpExecute(CorePlayer player, String warpName) {

        try {
            Warp warp = getWarpService().getWarp(warpName);
            getWarpService().deleteWarp(warp);
            player.info("Warp" + warp.getName() + " was deleted").handle();
        } catch (RuntimeException e) {
            player.warn(e.getMessage()).handle();
        }
    }

    private void setWarp(CorePlayer player, String warpName) {

        try{
            Warp warp = getWarpService().getWarp(warpName);
            warp.setLocation(player.getLocation());
            Warp savedWarp = getWarpService().updateWarp(warp);
        }catch (RuntimeException re){
            Warp warp = new Warp();
            warp.setLocation(player.getLocation());
            warp.setName(warpName);
            getWarpService().saveWarp(warp);
        }

        player.info("Warp has been set to your current location").handle();

    }
}


