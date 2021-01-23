package com.arematics.minecraft.maps.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.commands.SpawnCommand;
import com.arematics.minecraft.core.commands.WarpCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.controller.MapController;
import com.arematics.minecraft.data.mode.model.GameMap;
import com.arematics.minecraft.data.service.MapService;
import com.arematics.minecraft.data.service.WarpService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "spawn.map", description = "Permission for spawn command in map plugin")
public class MapSpawnCommand extends SpawnCommand {

    private final MapService mapService;
    private final MapController mapController;

    @Autowired
    public MapSpawnCommand(MapService mapService,
                           MapController mapController,
                           WarpCommand warpCommand,
                           WarpService warpService) {
        super(warpCommand, warpService);
        this.mapService = mapService;
        this.mapController = mapController;
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {
        if (!(sender instanceof Player))
            throw new CommandProcessException("Only Player can perform this command");
        CorePlayer player = CorePlayer.get((Player) sender);

        try{
            GameMap map = this.mapService.findById(this.mapController.getCurrentMapId());
            player.teleport(map.getLocation());
        }catch (Exception e){
            super.onDefaultExecute(sender);
        }
    }

    @SubCommand("setspawn {map}")
    @Perm(permission = "setspawn", description = "Set spawn point for map")
    public void setMapSpawn(CorePlayer player, String mapId) {
        GameMap map;
        try{
            map = this.mapService.findById(mapId);
        }catch (Exception e){
            map = new GameMap();
            map.setId(mapId);
        }
        map.setLocation(player.getLocation());
        this.mapService.save(map);
        player.info("Spawn for map: " + mapId + " has been set to your location").handle();
    }
}
