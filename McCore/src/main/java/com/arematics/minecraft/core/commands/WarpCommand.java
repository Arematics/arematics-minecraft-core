package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.MSGBuilder;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.Warp;
import com.arematics.minecraft.data.service.WarpService;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Getter
@Perm(permission = "warp", description = "warp to spot")
public class WarpCommand extends CoreCommand {

    private final WarpService warpService;

    @Autowired
    public WarpCommand(WarpService warpService) { super("warp"); this.warpService = warpService; }

    @Override
    protected boolean onDefaultCLI(CommandSender sender) {
        if(!(sender instanceof Player)) return true;
        CorePlayer player = CorePlayer.get((Player) sender);
        MSG items = MSGBuilder.join(warpService.fetchAllWarps()
                .stream()
                .map(this::toPart)
                .collect(Collectors.toList()), ',');
        player.info("listing")
                .setInjector(AdvancedMessageInjector.class)
                .replace("list_type", new Part("Warp"))
                .replace("list_value", items)
                .handle();
        return true;
    }

    private Part toPart(Warp warp){
        return PartBuilder.createHoverAndSuggest(warp.getName(),
                "§aTeleport to warp", "/warp " + warp.getName());
    }

    @SubCommand("{warp}")
    @Perm(permission = "to", description = "set Warp")
    public void warpTo(CorePlayer player, Warp warp) {
        teleport(player, warp, false);
        player.info("You were teleported to warp " + warp.getName()).handle();
    }

    public void teleport(CorePlayer player, Warp warp, boolean instant){
        player.teleport(warp.getLocation(), instant).schedule();
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
        Warp warp;
        try{
            warp = getWarpService().getWarp(warpName);
        }catch (RuntimeException re){
            warp = new Warp(warpName, player.getLocation());
        }
        warp.setLocation(player.getLocation());
        warpService.saveWarp(warp);

        player.info("Warp has been set to your current location").handle();

    }
}


