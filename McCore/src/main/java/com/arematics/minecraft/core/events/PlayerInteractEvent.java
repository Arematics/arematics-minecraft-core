package com.arematics.minecraft.core.events;

import com.arematics.minecraft.core.server.entities.InteractType;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;

@Setter
@Getter
public class PlayerInteractEvent extends PlayerEvent implements Cancellable {

    private final InteractType type;
    private final Action action;
    private final Block block;
    private final BlockFace blockFace;
    private final Result useClickedBlock;
    private final Result useItemInHand;
    private boolean isCancelled;

    public PlayerInteractEvent(CorePlayer player, InteractType type, Action action, Block block, BlockFace blockFace, Result useClickedBlock, Result useItemInHand) {
        super(player, false);
        this.type = type;
        this.action = action;
        this.block = block;
        this.blockFace = blockFace;
        this.useClickedBlock = useClickedBlock;
        this.useItemInHand = useItemInHand;
    }
}
