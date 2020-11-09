package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.language.Language;
import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.language.LanguageUser;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.*;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class LanguageCommand extends CoreCommand {


    public LanguageCommand(){
        super("language", "lang", "sprache");
    }

    @Override
    public void onDefaultExecute(CommandSender sender){
        MSG languages = MSGBuilder.join(',', asPart(sender, "EN"), asPart(sender, "DE"));
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .replace("cmd_usage", languages)
                .handle();
    }

    private Part asPart(CommandSender sender, String language){
        return new Part(language)
                .setHoverAction(HoverAction.SHOW_TEXT, LanguageAPI.prepareRawMessage(sender, "language_change_to")
                        .replaceAll("%language%", language))
                .setClickAction(ClickAction.RUN_COMMAND, "/language " + language);
    }

    @SubCommand("{language}")
    public boolean changeLanguage(Player player, String language) {
        if(language.equalsIgnoreCase("EN") || language.equals("ENGLISH") || language.equals("ENGLISCH"))
            language = "ENGLISH";
        if(language.equalsIgnoreCase("DE") || language.equals("DEUTSCH") || language.equals("GERMAN"))
            language = "DEUTSCH";
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
}
