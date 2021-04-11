package com.arematics.minecraft.core.server.entities.player.inventories;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.service.InventoryService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class WrappedInventory {

    private final InventoryService inventoryService;
    private Inventory open;
    private final String key;
    private final boolean global;
    private final Map<CorePlayer, OpenStrategy> viewers = new HashMap<>();

    public void closeForAll(){
        Set<CorePlayer> players = this.viewers.keySet();
        players.forEach(player -> {
            if(player.getPlayer().getOpenInventory().getTopInventory() != null) player.getPlayer().closeInventory();
            closeFor(player);
        });
    }

    public void closeFor(CorePlayer player){
        viewers.remove(player);
        if(viewers.isEmpty()) tearDown();
    }

    public void openFor(CorePlayer player, OpenStrategy strategy){
        if(!viewers.containsKey(player)) viewers.put(player, strategy);
        strategy.openInventory(open, player);
    }

    /**
     * Change contents of inventory. If inventory update from other server is executed this is needed
     * @param contents Contents
     */
    public void setContents(CoreItem[] contents){
        this.open.setContents(contents);
    }

    public void tearDown(){
        inventoryService.save(this);
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
