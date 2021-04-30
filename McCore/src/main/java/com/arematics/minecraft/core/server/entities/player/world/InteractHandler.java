package com.arematics.minecraft.core.server.entities.player.world;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Accessors(fluent = true)
@Getter
@Setter
@RequiredArgsConstructor
public class InteractHandler {

    private static final Logger logger = Bukkit.getLogger();

    private final CorePlayer player;

    private boolean inFight = false;
    private BukkitTask inFightTask;
    private BukkitTask inTeleport;

    public CoreItem getItemInHand(){
        return CoreItem.create(player.getPlayer().getItemInHand());
    }

    public void setItemInHand(ItemStack item){
        player.getPlayer().setItemInHand(item);
    }

    public void setInFight(){
        logger.config("Enable fight for player " + player.getName());
        this.inFight = true;
        if(inFightTask != null) inFightTask.cancel();
        this.inFightTask = ArematicsExecutor.asyncDelayed(this::fightEnd, 7, TimeUnit.SECONDS);
    }

    public void fightEnd(){
        logger.config("Ending fight for player " + player.getName());
        if(inFight) player.info("Could log out now").handle();
        this.inFight = false;
    }

    public Entity next(){
        return next(5);
    }

    public Entity next(int range){
        List<Entity> entities = player.getPlayer().getNearbyEntities(range, range, range);
        if(entities.isEmpty()) return null;
        return entities.get(0);
    }

    public void removeAmountFromHand(int amount){
        ArematicsExecutor.syncRun(() -> syncRemoveFromHand(amount));
    }

    private void syncRemoveFromHand(int amount){
        if(getItemInHand() != null)
            if(!player.getPlayer().getGameMode().equals(GameMode.CREATIVE)){
                int am = player.getPlayer().getItemInHand().getAmount();
                if(amount >= am)
                    player.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                else
                    player.getPlayer().getItemInHand().setAmount(am - amount);
            }
    }

    public void addPotionEffect(PotionEffect potionEffect) {
        player.getPlayer().addPotionEffect(potionEffect);
    }

    public boolean hasEffect(PotionEffectType type) {
        return player.getPlayer().hasPotionEffect(type);
    }

    public void removePotionEffect(PotionEffectType type) {
        player.getPlayer().removePotionEffect(type);
    }

    public void dispatchCommand(String command){
        ArematicsExecutor.syncRun(() -> player.getPlayer().performCommand(command.replaceFirst("/", "")));
    }

    @SuppressWarnings("unused")
    public void equip(CoreItem... items){
        ArematicsExecutor.runAsync(() -> this.equipItems(items));
    }

    private void equipItems(CoreItem... items){
        CoreItem[] drop = noUse(items);
        if(drop.length > 0){
            player.warn("" + drop.length + " items have been dropped").handle();
            Arrays.stream(drop).forEach(this::dropItem);
        }
    }

    public void dropItem(CoreItem drop){
        ArematicsExecutor.syncRun(() -> player.getLocation().getWorld().dropItemNaturally(player.getLocation(), drop));
    }

    private CoreItem[] noUse(CoreItem... item){
        return Arrays.stream(item)
                .filter(this::equipArmor)
                .toArray(CoreItem[]::new);
    }

    private boolean equipArmor(CoreItem item) {
        return hasEffect(PotionEffectType.INVISIBILITY);
    }

    public void stopTeleport(){
        if(inTeleport != null){
            inTeleport.cancel();
            player.warn("Your teleport has been cancelled").handle();
            inTeleport = null;
        }
    }

    public TeleportScheduler teleport(Location location, boolean instant){
        return new TeleportScheduler(player, location, instant);
    }

    public TeleportScheduler teleport(Location location){
        return teleport(location, false);
    }

    public TeleportScheduler instantTeleport(Location location){
        return teleport(location, true);
    }

}
