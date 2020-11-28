package com.arematics.minecraft.crystals.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.crystals.commands.parser.CrystalKeyParser;
import com.arematics.minecraft.crystals.logic.CrystalMetaParser;
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

import java.security.SecureRandom;
import java.util.*;

@Component
public class CrystalArmorStandClickListener implements Listener {

    private static final Random RANDOM = new Random(Short.MAX_VALUE);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final List<CorePlayer> open = new ArrayList<>();

    private final CrystalKeyParser parser;
    private final InventoryService service;
    private final CrystalMetaParser crystalMetaParser;

    @Autowired
    public CrystalArmorStandClickListener(CrystalKeyParser crystalKeyParser,
                                          InventoryService inventoryService,
                                          CrystalMetaParser crystalMetaParser){
        this.parser = crystalKeyParser;
        this.service = inventoryService;
        this.crystalMetaParser = crystalMetaParser;
    }

    @EventHandler
    public void onClick(PlayerInteractAtEntityEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        Entity entity = event.getRightClicked();

        if(!open.contains(player) && entity instanceof ArmorStand){
            ArmorStand stand = (ArmorStand)entity;

            CrystalKeyParser parser = Boots.getBoot(CoreBoot.class).getContext().getBean(CrystalKeyParser.class);

            Optional<CrystalKey> key = parser.readFromArmorStand(stand);
            key.ifPresent(value -> ArematicsExecutor.runAsync(() -> openCrystal(player, value)));
        }
    }

    private boolean checkKeySame(CrystalKey armorKey, CorePlayer player){
        Optional<CrystalKey> itemKey = parser.readFromItem(player.getItemInHand());
        return itemKey.filter(armorKey::equals).isPresent();
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof ArmorStand){
            CorePlayer player = CorePlayer.get((Player) event.getDamager());
            ArmorStand stand = (ArmorStand)event.getEntity();
            Optional<CrystalKey> key = parser.readFromArmorStand(stand);
            key.ifPresent(value -> openInventory(player, value));
        }
    }

    private void openInventory(CorePlayer player, CrystalKey key){

        Inventory inv = service.getOrCreate("crystal.inventory." + key.getName(), "§7Crystal " +
                key.getTotalName(), (byte)27);
        if(player.isIgnoreMeta()) player.openLowerEnabledInventory(inv);
        else player.openTotalBlockedInventory(inv);
    }

    private void openCrystal(CorePlayer player, CrystalKey key){
        if(checkKeySame(key, player)){
            open.add(player);
            player.removeAmountFromHand(1);
            this.crystalMetaParser.parse(player, calculate(key));
            open.remove(player);
        }else
            player.warn("Not the same keys provided").handle();
    }

    private CoreItem calculate(CrystalKey key){
        CoreItem[] contents = CoreItem.create(service.getOrCreate("crystal.inventory." +
                key.getName(), "§7Crystal " +
                key.getTotalName(), (byte)27).getContents());
        final ArrayList<CoreItem> itemStacks = new ArrayList<>();

        for(int i = 0; i < contents.length; i++){
            CoreItem item = contents[i];
            if(item == null) continue;
            try{
                double value = getChance(item);

                int put = (int)(value*100);
                itemStacks.ensureCapacity(put);

                while(put != 0){
                    itemStacks.add(item);
                    put--;
                }
            }catch (NumberFormatException ignored){}
        }

        Collections.shuffle(itemStacks, CrystalArmorStandClickListener.RANDOM);
        return itemStacks.get(CrystalArmorStandClickListener.SECURE_RANDOM.nextInt(itemStacks.size()));
    }

    private static double getChance(CoreItem item) throws NumberFormatException{
        return Double.parseDouble(item.getMeta().getString("chance"));
    }
}
