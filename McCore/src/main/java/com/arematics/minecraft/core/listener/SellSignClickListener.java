package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class SellSignClickListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null &&
                (event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.WALL_SIGN)){
            Sign clicked = (Sign) event.getClickedBlock().getState();
            if(clicked.getLine(0).equals("§8[§bSELL§8]")){
                String rawPrice = clicked.getLine(1)
                        .replaceAll("Coins", "")
                        .replaceAll(" ", "");
                try{
                    double price = Double.parseDouble(rawPrice);
                    if(player.getMoney() >= price){
                        Location loc = event.getClickedBlock().getLocation().add(0.5, 1, 0.5);
                        Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5);
                        entities = entities.stream().filter(entity -> entity instanceof ItemFrame).collect(Collectors.toList());
                        if(!entities.isEmpty()){
                            ItemFrame frame = (ItemFrame) entities.stream().findFirst().orElse(null);
                            CoreItem item = CoreItem.create(frame.getItem());
                            player.getPlayer().getInventory().addItem(item);
                            player.removeMoney(price);
                        }
                    }else
                        player.warn("You dont have enough money to afford this").handle();
                }catch (Exception ignore){
                    player.warn("Not a valid pricing").handle();
                }
            }
        }
    }
}
