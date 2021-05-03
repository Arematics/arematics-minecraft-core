package com.arematics.minecraft.core.server.entities.player.inventories;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class WrappedInventory {

    private Inventory open;
    private final String key;
    private final boolean global;
    private final Map<CorePlayer, OpenStrategy> viewers = new HashMap<>();

    public void closeFor(CorePlayer player){
        viewers.remove(player);
    }

    public void addViewer(CorePlayer player, OpenStrategy openStrategy){
        this.viewers.put(player, openStrategy);
    }

    public Map<CorePlayer, OpenStrategy> viewers() {
        return viewers;
    }

    /**
     * Change contents of inventory. If inventory update from other server is executed this is needed
     * @param contents Contents
     */
    public void setContents(CoreItem[] contents){
        this.open.setContents(contents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WrappedInventory that = (WrappedInventory) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
