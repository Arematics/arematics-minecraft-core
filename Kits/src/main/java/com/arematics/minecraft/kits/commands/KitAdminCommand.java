package com.arematics.minecraft.kits.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.core.utils.TimeUtils;
import com.arematics.minecraft.data.mode.model.Kit;
import com.arematics.minecraft.data.service.InventoryService;
import com.arematics.minecraft.data.service.KitService;
import com.arematics.minecraft.data.share.model.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Perm(permission = "kitadm", description = "Permission to full Kit Administration Command")
public class KitAdminCommand extends CoreCommand {
    private final String KIT_NOT_FOUND = "kit_not_found";
    private final String KIT_DELETED = "kit_deleted";
    private final String KIT_CREATED = "kit_created";
    private final String KIT_ALREADY_EXISTS = "kit_already_exists";
    private final String KIT_TYPE_CHANGED = "kit_type_changed";

    private final KitService service;
    private final InventoryService inventoryService;

    @Autowired
    public KitAdminCommand(KitService kitService, InventoryService inventoryService){
        super("kitadm", "kitmgr");
        this.service = kitService;
        this.inventoryService = inventoryService;
    }

    @SubCommand("info {kit}")
    public boolean sendKitInfo(CommandSender sender, Kit kit) {
        prettyPrintKitInfo(sender, kit);
        return true;
    }

    private void prettyPrintKitInfo(CommandSender sender, Kit kit){
        String msg = "§a\n\n§7Kit" + " » " + "§c/" + kit.getName() + "\n" +
                "%information%";
        List<Part> parts = new ArrayList<>();
        parts.add(new Part("     §7" + "Permission: " + " §c" +
                (kit.getPermission() == null ? "Keine" : kit.getPermission().getPermission()) + "\n")
                .setHoverAction(HoverAction.SHOW_TEXT, "§7Change Kit Permission")
                .setClickAction(ClickAction.SUGGEST_COMMAND, "/kitadm setPerm " + kit.getName() + " {permission}"));
        parts.add(new Part("     §7" + "Cooldown: " + " §c" +
                (TimeUtils.toString(Period.seconds((int) (kit.getCooldown()/1000))))  + "\n")
                .setHoverAction(HoverAction.SHOW_TEXT, "§7Change Kit Cooldown")
                .setClickAction(ClickAction.SUGGEST_COMMAND, "/kitadm setCooldown " + kit.getName() + " {cooldown}"));
        parts.add(new Part("     §7" + "Playtime: " + " §c" +
                (TimeUtils.toString(Period.seconds((int) (kit.getMinPlayTime()/1000))))  + "\n")
                .setHoverAction(HoverAction.SHOW_TEXT, "§7Change Kit Playtime")
                .setClickAction(ClickAction.SUGGEST_COMMAND, "/kitadm setPlaytime " + kit.getName() + " {playtime}"));
        Messages.create(msg)
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .replace("information", new MSG(parts))
                .disableServerPrefix()
                .handle();
    }

    @SubCommand("create {name} {permission} {cooldown}")
    public boolean createKit(Player player, String name, Permission permission, Period cooldown) {
        return createKit(player, name, permission, cooldown, Period.millis(1));
    }

    @SubCommand("create {name} {permission} {cooldown} {minPlayTime}")
    public boolean createKit(Player player, String name, Permission permission,
                             Period cooldown, Period minPlayTime) {
        try{
            service.findKit(name);
            Messages.create(KIT_ALREADY_EXISTS)
                    .WARNING()
                    .to(player)
                    .DEFAULT()
                    .replace("kitName", name)
                    .handle();
            return true;
        }catch (RuntimeException re){
            CoreItem itemStack = CoreItem.create(player.getItemInHand());
            if(itemStack != null) {
                Kit newKit = new Kit(null, name, permission, cooldown.toStandardDuration().getMillis(),
                        minPlayTime.toStandardDuration().getMillis(), new CoreItem[]{itemStack});
                service.update(newKit);
                Messages.create(KIT_CREATED)
                        .to(player)
                        .DEFAULT()
                        .replace("kitName", name)
                        .handle();
            }
            else Messages.create("no_item_in_hand").WARNING().to(player).handle();
            return true;
        }
    }

    @SubCommand("delete {kit}")
    @Perm(permission = "delete", description = "Permission to delete kit")
    public boolean deleteKit(CommandSender sender, Kit kit) {
        inventoryService.delete("kit.inventory." + kit.getName());
        service.delete(kit);
        Messages.create(KIT_DELETED)
                .to(sender)
                .DEFAULT()
                .replace("kitName", kit.getName())
                .handle();
        return true;
    }

    @SubCommand("edit {name}")
    public boolean editKit(Player player, String name) {
        try{
            Kit kit = service.findKit(name);
            Inventory inv = inventoryService.getOrCreate("kit.inventory." + kit.getName(), "§6Kit " + name,
                    (byte)27);
            ArematicsExecutor.syncRun(() -> player.openInventory(inv));
            return true;
        }catch (RuntimeException re){
            Messages.create(KIT_NOT_FOUND)
                    .WARNING()
                    .to(player)
                    .DEFAULT()
                    .replace("kitName", name)
                    .handle();
            return true;
        }
    }

    @SubCommand("setPerm {kit} {permission}")
    public boolean setKitPermission(CommandSender sender, Kit kit, Permission permission) {
        kit.setPermission(permission);
        service.update(kit);
        Messages.create(KIT_TYPE_CHANGED)
                .to(sender)
                .DEFAULT()
                .replace("type", "Permission")
                .replace("value", permission.getPermission())
                .handle();
        return true;
    }

    @SubCommand("setCooldown {kit} {cooldown}")
    public boolean setKitCooldown(CommandSender sender, Kit kit, Period cooldown) {
        kit.setCooldown(cooldown.toStandardDuration().getMillis());
        service.update(kit);
        Messages.create(KIT_TYPE_CHANGED)
                .to(sender)
                .DEFAULT()
                .replace("type", "Cooldown")
                .replace("value", TimeUtils.toString(cooldown))
                .handle();
        return true;
    }

    @SubCommand("setPlaytime {kit} {playtime}")
    public boolean setKitPlaytime(CommandSender sender, Kit kit, Period playtime) {
        kit.setMinPlayTime(playtime.toStandardDuration().getMillis());
        service.update(kit);
        Messages.create(KIT_TYPE_CHANGED)
                .to(sender)
                .DEFAULT()
                .replace("type", "Playtime")
                .replace("value", TimeUtils.toString(playtime))
                .handle();
        return true;
    }
}
