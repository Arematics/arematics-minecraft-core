package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.language.Language;
import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.language.LanguageUser;
import com.arematics.minecraft.core.messaging.advanced.*;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.InventoryHandler;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryController;
import com.arematics.minecraft.core.server.entities.player.inventories.WrappedInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class LanguageCommand extends CoreCommand {

    private final LanguageAPI languageAPI;
    private final InventoryController inventoryController;

    @Autowired
    public LanguageCommand(LanguageAPI languageAPI, InventoryController inventoryController){
        super("language", true, "lang", "sprache");
        this.languageAPI = languageAPI;
        this.inventoryController = inventoryController;
    }

    @Override
    protected void onDefaultCLI(CorePlayer sender){
        MSG languages = MSGBuilder.join(',', asPart(sender, "EN"), asPart(sender, "DE"));
        sender.info("cmd_not_valid")
                .setInjector(AdvancedMessageInjector.class)
                .replace("cmd_usage", languages)
                .handle();
    }

    @Override
    protected void onDefaultGUI(CorePlayer player){
        WrappedInventory inv = inventoryController.findOrCreate("language.default.selection", "§9Language", (byte) 9);
        if(player.ignoreMeta()){
            player.handle(InventoryHandler.class).openLowerEnabledInventory(inv);
            return;
        }
        CoreItem[] items = Arrays.stream(CoreItem.create(inv.getOpen().getContents()))
                .map(item -> process(player.getPlayer(), item))
                .toArray(CoreItem[]::new);
        Inventory clone = Bukkit.createInventory(null, (byte) 9, "§9Language");
        clone.setContents(items);
        player.handle(InventoryHandler.class).openInventory(clone);
    }

    private CoreItem process(Player player, CoreItem item){
        if(item == null) return null;
        String value = item.readMetaValue("language");
        if(value != null){
            value = mapLanguageInput(value);
            Language language = languageAPI.getUser(player).getLanguage();
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

    private Part asPart(CorePlayer sender, String language){
        return new Part(language)
                .setHoverAction(HoverAction.SHOW_TEXT, languageAPI.prepareRawMessage(sender.getPlayer(), "language_change_to")
                        .replaceAll("%language%", language))
                .setClickAction(ClickAction.RUN_COMMAND, "/language " + language);
    }

    @SubCommand("{language}")
    public boolean changeLanguage(CorePlayer player, String language) {
        language = mapLanguageInput(language);
        Language finalLanguage = languageAPI.getLanguage(language);
        if(finalLanguage == null){
            player.warn("language_not_found").handle();
            return true;
        }
        LanguageUser user = languageAPI.getUser(player.getPlayer());
        user.setLanguage(finalLanguage);
        player.info("language_changed")
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
