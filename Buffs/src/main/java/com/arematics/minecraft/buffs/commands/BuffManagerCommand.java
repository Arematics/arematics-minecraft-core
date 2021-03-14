package com.arematics.minecraft.buffs.commands;

import com.arematics.minecraft.buffs.server.PlayerBuffHandler;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.supplier.standard.CommandSupplier;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.entities.player.inventories.PageBinder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Range;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.data.BuffListMode;
import com.arematics.minecraft.data.mode.model.PlayerBuff;
import com.arematics.minecraft.data.service.PlayerBuffService;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class BuffManagerCommand extends CoreCommand {

    private final PlayerBuffService service;
    private final PlayerBuffHandler handler;

    @Autowired
    public BuffManagerCommand(PlayerBuffService playerBuffService, PlayerBuffHandler playerBuffHandler){
        super("buffs", "buff", "permabuffs");
        this.service = playerBuffService;
        this.handler = playerBuffHandler;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        fetchBuffsByMode(sender, BuffListMode.ALL);
    }

    @SubCommand("fetch {mode}")
    public void fetchBuffsByMode(CorePlayer sender, BuffListMode mode) {
        Supplier<Page<PlayerBuff>> paging = () -> this.service
                .findBuffsByPlayer(sender.getUUID(),
                        sender.inventories().getPage(),
                        () -> sender.inventories().getEnumOrDefault(mode));
        CommandSupplier.create()
                .setCLI(player -> cliQuery())
                .setGUI(player -> createInventory(player, paging, mode))
                .accept(sender);
    }

    @SubCommand("activate {type}")
    public void activateBuff(CorePlayer sender, PotionEffectType potionEffectType) {
        try{
            PlayerBuff buff = service.findValidBuff(sender.getUUID(), potionEffectType);
            handler.addPotionEffectToPlayer(sender, buff);
            buff.setActive(true);
            service.update(buff);
            sender.info("Buff " + potionEffectType.getName() + " has been activated").handle();
        }catch (RuntimeException re){
            sender.warn("Not owning this buff").handle();
        }
    }

    @SubCommand("deActivate {type}")
    public void deActivateBuff(CorePlayer sender, PotionEffectType potionEffectType) {
        try{
            PlayerBuff buff = service.findValidBuff(sender.getUUID(), potionEffectType);
            handler.removePotionEffectFromPlayer(sender, potionEffectType);
            buff.setActive(false);
            service.update(buff);
            sender.info("Buff " + potionEffectType.getName() + " has been deactivated").handle();
        }catch (RuntimeException re){
            sender.warn("Not owning this buff").handle();
        }
    }

    private void cliQuery(){
        System.out.println("Coming soon");
    }

    private void createInventory(CorePlayer sender, Supplier<Page<PlayerBuff>> paging, BuffListMode mode){
        Range range = Range.allHardInRows(1, 7, 1);
        PageBinder<PlayerBuff> binder = PageBinder.of(paging, range, this::mapPlayerBuff);
        CoreItem modeItem = CoreItem.generate(Material.HOPPER)
                .setName("§cQuery Mode")
                .bindEnumLore(mode);
        InventoryBuilder builder = InventoryBuilder.create("§6Buffs", 3)
                .openBlocked(sender)
                .fillOuterLine()
                .bindPaging(sender, binder, true)
                .addItem(modeItem, 1, 5);
        sender.inventories().registerEnumItemClickWithRefresh(modeItem, mode, builder, binder);
    }

    private CoreItem mapPlayerBuff(PlayerBuff playerBuff){
        String subCommand = playerBuff.isActive() ? "deActivate" : "activate";
        String active = playerBuff.isActive() ? "§aYes" : "§cNo";
        String perm = playerBuff.getEndTime() == null ? "§aYes" : "§cNo";
        String endTime = playerBuff.getEndTime() == null ? "§cNever" :
                "§a" + TimeUtils.fetchEndDate(playerBuff.getEndTime());
        return CoreItem.generate(Material.POTION)
                .bindCommand("buffs " + subCommand + " " + playerBuff.getPotionEffectType())
                .setName("§cBuff: " + playerBuff.getPotionEffectType())
                .addToLore("§8Strength: " + (playerBuff.getStrength() + 1))
                .addToLore("§8Active: " + active)
                .addToLore("§8Permanent: " + perm)
                .addToLore("§8Ending: " + endTime);
    }
}
