package com.arematics.minecraft.crystals.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.crystals.commands.parser.CrystalKeyParser;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import com.arematics.minecraft.data.service.InventoryService;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CrystalArmorStandClickListener implements Listener {

    private static final List<CorePlayer> open = new ArrayList<>();

    private final InventoryService service;

    @Autowired
    public CrystalArmorStandClickListener(InventoryService inventoryService){
        this.service = inventoryService;
    }

    @EventHandler
    public void onClick(PlayerInteractAtEntityEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        Entity entity = event.getRightClicked();

        if(!open.contains(player) && entity instanceof ArmorStand){
            ArmorStand stand = (ArmorStand)entity;

            CrystalKeyParser parser = Boots.getBoot(CoreBoot.class).getContext().getBean(CrystalKeyParser.class);

            try{
                CrystalKey key = parser.readFromArmorStand(stand);
            }catch (RuntimeException ignored){ }
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof ArmorStand){
            CorePlayer player = CorePlayer.get((Player) event.getDamager());
            ArmorStand stand = (ArmorStand)event.getEntity();

            CrystalKeyParser parser = Boots.getBoot(CoreBoot.class).getContext().getBean(CrystalKeyParser.class);

            try{
                CrystalKey key = parser.readFromArmorStand(stand);
                Inventory inv = service.getOrCreate("crystal.inventory." + key.getName(), "§7Crystal " +
                                key.getTotalName(),
                        (byte)27);
                if(player.isIgnoreMeta()) player.openLowerEnabledInventory(inv);
                else player.openTotalBlockedInventory(inv);
            }catch (RuntimeException ignored){ }
        }
    }
}
