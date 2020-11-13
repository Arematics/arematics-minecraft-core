package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.language.Language;
import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.language.LanguageUser;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.*;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.service.InventoryService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class LanguageCommand extends CoreCommand {

    private final InventoryService service;

    @Autowired
    public LanguageCommand(InventoryService inventoryService){
        super("language", "lang", "sprache");
        this.service = inventoryService;
    }

    @Override
    protected boolean onDefaultCLI(CommandSender sender){
        MSG languages = MSGBuilder.join(',', asPart(sender, "EN"), asPart(sender, "DE"));
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .replace("cmd_usage", languages)
                .handle();
        return true;
    }

    @Override
    protected boolean onDefaultUI(CorePlayer player){
        Inventory inv = service.getOrCreate("language.default.selection", "§9Language", (byte) 9);
        if(player.isIgnoreMeta()){
            ArematicsExecutor.syncRun(() -> player.getPlayer().openInventory(inv));
            return true;
        }
        CoreItem[] items = Arrays.stream(CoreItem.create(inv.getContents()))
                .map(item -> process(player.getPlayer(), item))
                .toArray(CoreItem[]::new);
        Arrays.stream(items).forEach(System.out::println);
        Inventory clone = Bukkit.createInventory(null, (byte) 9, "§9Language");
        clone.setContents(items);
        ArematicsExecutor.syncRun(() -> player.getPlayer().openInventory(clone));
        return true;
    }

    private CoreItem process(Player player, CoreItem item){
        if(item == null) return null;
        String value = item.readMetaValue("language");
        if(value != null){
            value = mapLanguageInput(value);
            Language language = LanguageAPI.getUser(player).getLanguage();
            item = item.setName("§aLanguage: §c" + value);
            if(language.getName().equals(value)){
                return item.unbindCommand().disableClick()
                        .setName("§aLanguage: §c" + value)
                        .setGlow()
                        .addToLore("§aSelected Language");
            }
        }
        return item;
    }

    private Part asPart(CommandSender sender, String language){
        return new Part(language)
                .setHoverAction(HoverAction.SHOW_TEXT, LanguageAPI.prepareRawMessage(sender, "language_change_to")
                        .replaceAll("%language%", language))
                .setClickAction(ClickAction.RUN_COMMAND, "/language " + language);
    }

    @SubCommand("{language}")
    public boolean changeLanguage(Player player, String language) {
        language = mapLanguageInput(language);
        Language finalLanguage = LanguageAPI.getLanguage(language);
        if(finalLanguage == null){
            Messages.create("language_not_found").WARNING().to(player).handle();
            return true;
        }
        LanguageUser user = LanguageAPI.getUser(player);
        user.setLanguage(finalLanguage);
        Messages.create("language_changed")
                .to(player)
                .DEFAULT()
                .replace("language", finalLanguage.getName())
                .handle();
        return true;
    }

    private String mapLanguageInput(String language){
        if(language.equalsIgnoreCase("EN") || language.equals("ENGLISH") || language.equals("ENGLISCH"))
            return  "ENGLISH";
        if(language.equalsIgnoreCase("DE") || language.equals("DEUTSCH") || language.equals("GERMAN"))
            return  "DEUTSCH";
        return "ENGLISH";
    }
}
