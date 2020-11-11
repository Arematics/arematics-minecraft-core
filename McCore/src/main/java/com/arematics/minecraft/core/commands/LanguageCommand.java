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
import com.arematics.minecraft.data.service.InventoryService;
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
    protected boolean onCLI(CommandSender sender){
        MSG languages = MSGBuilder.join(',', asPart(sender, "EN"), asPart(sender, "DE"));
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .replace("cmd_usage", languages)
                .handle();
        return true;
    }

    @Override
    protected boolean onUI(Player player){
        Inventory inv = service.getOrCreate("language.default.selection", "§9Language", (byte) 9);
        CoreItem[] items = CoreItem.create(inv.getContents());
        items = Arrays.stream(items).map(item -> process(player, item)).toArray(CoreItem[]::new);
        return true;
    }

    private CoreItem process(Player player, CoreItem item){
        String value = item.readMetaValue("language");
        if(value != null){
            value = mapLanguageInput(value);
            Language language = LanguageAPI.getUser(player).getLanguage();
            item = item.setName("§aLanguage: §c" + value);
            if(language.getName().equals(value)){
                return item.setGlow().addToLore("§aSelected Language");
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
