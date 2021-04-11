package com.arematics.minecraft.maps.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.commands.SpawnCommand;
import com.arematics.minecraft.core.commands.WarpCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.controller.MapController;
import com.arematics.minecraft.data.mode.model.GameMap;
import com.arematics.minecraft.data.service.MapService;
import com.arematics.minecraft.data.service.WarpService;
import org.bukkit.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "world.interact.spawn", description = "Map plugin spawn command permission")
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
    public void onDefaultExecute(CorePlayer sender) {
        try{
            GameMap map = this.mapService.findById(this.mapController.getCurrentMapId());
            sender.teleport(map.getLocation()).schedule();
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

    public Location findCurrentSpawn(){
        try{
            GameMap map = this.mapService.findById(this.mapController.getCurrentMapId());
            return map.getLocation();
        }catch (Exception e){
            return getWarpService().getWarp("spawn").getLocation();
        }
    }
}
