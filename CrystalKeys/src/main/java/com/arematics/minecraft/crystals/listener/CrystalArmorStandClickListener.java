package com.arematics.minecraft.crystals.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.WrappedInventory;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.crystals.commands.parser.CrystalKeyParser;
import com.arematics.minecraft.crystals.logic.CrystalKeyItem;
import com.arematics.minecraft.crystals.logic.CrystalMetaParser;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import com.arematics.minecraft.data.service.InventoryService;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(PlayerInteractAtEntityEvent event){
        CorePlayer player = CorePlayer.get(event.getPlayer());
        Entity entity = event.getRightClicked();

        if(!open.contains(player) && entity instanceof ArmorStand){
            ArmorStand stand = (ArmorStand)entity;

            CrystalKeyParser parser = Boots.getBoot(CoreBoot.class).getContext().getBean(CrystalKeyParser.class);

            Optional<CrystalKey> key = parser.readFromArmorStand(stand);
            key.ifPresent(value -> ArematicsExecutor.runAsync(() -> openCrystal(player, value)));
            event.setCancelled(true);
        }
    }

    private boolean checkKeySame(CrystalKey armorKey, CorePlayer player){
        Optional<CrystalKey> itemKey = parser.readFromItem(player.interact().getItemInHand());
        return itemKey.filter(armorKey::equals).isPresent();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHit(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof ArmorStand){
            CorePlayer player = CorePlayer.get((Player) event.getDamager());
            ArmorStand stand = (ArmorStand)event.getEntity();
            Optional<CrystalKey> key = parser.readFromArmorStand(stand);
            key.ifPresent(value -> openInventory(player, value));
            event.setCancelled(true);
        }
    }

    private void openInventory(CorePlayer player, CrystalKey key){

        WrappedInventory inv = service.findOrCreate("crystal.inventory." + key.getName(), "§7Crystal " +
                key.getTotalName(), (byte)27);
        if(player.ignoreMeta()) player.inventories().openLowerEnabledInventory(inv);
        else player.inventories().openTotalBlockedInventory(inv);
    }

    private void openCrystal(CorePlayer player, CrystalKey key){
        if(Arrays.stream(player.getPlayer().getInventory().getContents()).filter(Objects::nonNull).count() >= 36){
            player.warn("Your inventory is full, please free some slots").handle();
            return;
        }
        if(checkKeySame(key, player)){
            open.add(player);
            player.interact().removeAmountFromHand(1);
            CoreItem[] contents = CoreItem.create(service.findOrCreate("crystal.inventory." +
                    key.getName(), "§7Crystal " +
                    key.getTotalName(), (byte)27).getOpen().getContents());
            if(Arrays.stream(contents).filter(content -> content != null && content.getType() != Material.AIR).count() <= 0){
                player.warn("Key has no items to filter").handle();
                open.remove(player);
                return;
            }
            try{
                this.crystalMetaParser.parse(player, calculate(key), key);
            }catch (RuntimeException e){
                player.warn(e.getMessage()).handle();
                if(player.getPlayer().getGameMode() != GameMode.CREATIVE)
                    player.getPlayer().getInventory().addItem(CrystalKeyItem.fromKey(key));
            }
            open.remove(player);
        }else
            player.warn("Not the same keys provided").handle();
    }

    private CoreItem calculate(CrystalKey key){
        CoreItem[] contents = CoreItem.create(service.findOrCreate("crystal.inventory." +
                key.getName(), "§7Crystal " +
                key.getTotalName(), (byte)27).getOpen().getContents());
        final ArrayList<CoreItem> itemStacks = new ArrayList<>();
        if(contents.length == 0) throw new CommandProcessException("No item found");

        for (CoreItem item : contents) {
            if (item == null) continue;
            try {
                double value = getChance(item);

                int put = (int) (value * 100);
                itemStacks.ensureCapacity(put);

                while (put != 0) {
                    itemStacks.add(item);
                    put--;
                }
            } catch (NumberFormatException ignored) {}
        }

        Collections.shuffle(itemStacks, CrystalArmorStandClickListener.RANDOM);
        return itemStacks.get(CrystalArmorStandClickListener.SECURE_RANDOM.nextInt(itemStacks.size()));
    }

    private static double getChance(CoreItem item) throws NumberFormatException{
        return Double.parseDouble(item.getMeta().getString("chance"));
    }
}
