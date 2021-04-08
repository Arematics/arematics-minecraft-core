package com.arematics.minecraft.kits.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.WrappedInventory;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.data.mode.model.Kit;
import com.arematics.minecraft.data.service.InventoryService;
import com.arematics.minecraft.data.service.ItemCollectionService;
import com.arematics.minecraft.data.service.KitService;
import org.bukkit.command.CommandSender;
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
    private final ItemCollectionService itemCollectionService;

    @Autowired
    public KitAdminCommand(KitService kitService,
                           InventoryService inventoryService,
                           ItemCollectionService itemCollectionService){
        super("kitadm", true, "kitmgr");
        this.service = kitService;
        this.inventoryService = inventoryService;
        this.itemCollectionService = itemCollectionService;
    }

    @SubCommand("info {kit}")
    public boolean sendKitInfo(CorePlayer player, Kit kit) {
        prettyPrintKitInfo(player, kit);
        return true;
    }

    private void prettyPrintKitInfo(CorePlayer player, Kit kit){
        String msg = "§a\n\n§7Kit" + " » " + "§c/" + kit.getName() + "\n" +
                "%information%";
        List<Part> parts = new ArrayList<>();
        parts.add(new Part("     §7" + "Permission: " + " §c" +
                (kit.getPermission() == null ? "Keine" : kit.getPermission()) + "\n")
                .setHoverAction(HoverAction.SHOW_TEXT, "§7Change Kit Permission")
                .setClickAction(ClickAction.SUGGEST_COMMAND, "/kitadm setPerm " + kit.getName() + " {permission}"));
        parts.add(new Part("     §7" + "Cooldown: " + " §c" +
                (TimeUtils.toRawString(kit.getCooldown()))  + "\n")
                .setHoverAction(HoverAction.SHOW_TEXT, "§7Change Kit Cooldown")
                .setClickAction(ClickAction.SUGGEST_COMMAND, "/kitadm setCooldown " + kit.getName() + " {cooldown}"));
        parts.add(new Part("     §7" + "Playtime: " + " §c" +
                (TimeUtils.toRawString(kit.getMinPlayTime()))  + "\n")
                .setHoverAction(HoverAction.SHOW_TEXT, "§7Change Kit Playtime")
                .setClickAction(ClickAction.SUGGEST_COMMAND, "/kitadm setPlaytime " + kit.getName() + " {playtime}"));
        player.info(msg)
                .setInjector(AdvancedMessageInjector.class)
                .replace("information", new MSG(parts))
                .disableServerPrefix()
                .handle();
    }

    @SubCommand("create {name} {permission} {cooldown}")
    public void createKit(CorePlayer player, String name, String permission, Period cooldown) {
        createKit(player, name, permission, cooldown, Period.millis(1));
    }

    @SubCommand("create {name} {permission} {cooldown} {minPlayTime}")
    public void createKit(CorePlayer player, String name, String permission,
                             Period cooldown, Period minPlayTime) {
        try{
            service.findKit(name);
            player.warn(KIT_ALREADY_EXISTS)
                    .DEFAULT()
                    .replace("kitName", name)
                    .handle();
        }catch (RuntimeException re){
            CoreItem itemStack = CoreItem.create(player.getItemInHand());
            if(itemStack != null) {
                try{
                    Kit newKit = new Kit(null, name, permission, cooldown.toStandardDuration().getMillis(),
                            minPlayTime.toStandardDuration().getMillis(), new CoreItem[]{itemStack});
                    service.update(newKit);
                    player.info(KIT_CREATED)
                            .DEFAULT()
                            .replace("kitName", name)
                            .handle();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                throw new CommandProcessException("no_item_in_hand");
            }
        }
    }

    @SubCommand("delete {kit}")
    @Perm(permission = "delete", description = "Permission to delete kit")
    public boolean deleteKit(CorePlayer sender, Kit kit) {
        inventoryService.remove("kit.inventory." + kit.getName());
        itemCollectionService.remove(itemCollectionService.findOrCreate("kit.inventory." + kit.getName()));
        service.delete(kit);
        sender.info(KIT_DELETED)
                .DEFAULT()
                .replace("kitName", kit.getName())
                .handle();
        return true;
    }

    @SubCommand("edit {name}")
    public boolean editKit(CorePlayer player, String name) {
        try{
            Kit kit = service.findKit(name);
            WrappedInventory inv = inventoryService.findOrCreate("kit.inventory." + kit.getName(), "§6Kit " + name,
                    (byte)27);
            player.inventories().openLowerEnabledInventory(inv);
            return true;
        }catch (RuntimeException re){
            player.warn(KIT_NOT_FOUND)
                    .DEFAULT()
                    .replace("kitName", name)
                    .handle();
            return true;
        }
    }

    @SubCommand("setPerm {kit} {permission}")
    public boolean setKitPermission(CommandSender sender, Kit kit, String permission) {
        kit.setPermission(permission);
        service.update(kit);
        Messages.create(KIT_TYPE_CHANGED)
                .to(sender)
                .DEFAULT()
                .replace("type", "Permission")
                .replace("value", permission)
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
