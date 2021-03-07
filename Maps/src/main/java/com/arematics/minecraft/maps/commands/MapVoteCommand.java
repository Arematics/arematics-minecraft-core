package com.arematics.minecraft.maps.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.CommandUtils;
import com.arematics.minecraft.data.controller.MapController;
import com.arematics.minecraft.data.mode.model.GameMap;
import com.arematics.minecraft.data.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MapVoteCommand extends CoreCommand {

    private final MapService service;
    private final MapController controller;

    @Autowired
    public MapVoteCommand(MapService mapService, MapController mapController) {
        super("mapvote", "mv");
        this.service = mapService;
        this.controller = mapController;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        String result = CommandUtils.prettyHeader("Map Vote", "Next Map");
        sender.info(result).DEFAULT().disableServerPrefix().handle();
        this.controller.getNextMapIds().forEach(mapId -> sendInteractMessage(sender, mapId));
    }

    private void sendInteractMessage(CorePlayer player, String mapId){
        Part part = new Part("    §a[✔] Vote for map: " + mapId + " | Votes: " +
                this.controller.getVotes().getOrDefault(mapId, 0));
        part.setHoverAction(HoverAction.SHOW_TEXT, "§aVote for map " + mapId)
                .setClickAction(ClickAction.RUN_COMMAND, "/mv vote " + mapId);
        player.info("%click_map%")
                .setInjector(AdvancedMessageInjector.class)
                .disableServerPrefix()
                .replace("click_map", part)
                .handle();
    }

    @SubCommand("vote {map}")
    public void voteForMap(CorePlayer player, String mapId) {
        if(this.controller.getVoted().contains(player))
            throw new CommandProcessException("Already voted for map");
        try{
            GameMap map = this.service.findById(mapId);
            this.controller.addVote(player, mapId);
            player.info("Voted for map: " + mapId).handle();
        }catch (Exception e){
            player.warn("This map not exists").handle();
        }
    }
}
