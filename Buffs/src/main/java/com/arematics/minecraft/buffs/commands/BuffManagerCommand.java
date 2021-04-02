package com.arematics.minecraft.buffs.commands;

import com.arematics.minecraft.buffs.server.PlayerBuffHandler;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.entities.player.inventories.PageBinder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Range;
import com.arematics.minecraft.core.server.entities.player.inventories.paging.Paging;
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
        Paging.create(sender, paging)
                .onCLI(this::buffPart, "Buff", "buffs fetch " + mode)
                .onGUI((player, pager) -> createInventory(player, pager, mode))
                .execute();
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

    private MSG buffPart(PlayerBuff buff){
        String subCommand = buff.isActive() ? "deActivate" : "activate";
        Part base = new Part(buff.getPotionEffectType()).setHoverActionShowItem(buff.mapToItem(server));
        Part mode = PartBuilder.createHoverAndRun("§8[" + (buff.isActive() ? "§cD" : "§aE") + "§8]",
                buff.isActive() ? "§cDisable buff" : "§aEnable buff",
                "/buffs " + subCommand + " " + buff.getPotionEffectType());
        return new MSG(base, mode);
    }

    private void createInventory(CorePlayer sender, Supplier<Page<PlayerBuff>> paging, BuffListMode mode){
        Range range = Range.allHardInRows(1, 7, 1);
        PageBinder<PlayerBuff> binder = PageBinder.of(paging, range, server);
        CoreItem modeItem = server.items().generateNoModifier(Material.HOPPER)
                .setName("§cQuery Mode")
                .bindEnumLore(mode);
        InventoryBuilder builder = InventoryBuilder.create("Buffs", 3)
                .openBlocked(sender)
                .fillOuterLine()
                .bindPaging(sender, binder, true)
                .addItem(modeItem, 1, 5);
        sender.inventories()
                .addRefresher(() -> builder.bindPaging(sender, binder, true))
                .enableRefreshTask()
                .registerEnumItemClickWithRefresh(modeItem, mode);
    }
}
