package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.language.Language;
import com.arematics.minecraft.core.language.LanguageAPI;
import com.arematics.minecraft.core.language.LanguageUser;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

@Component
public class LanguageCommand extends CoreCommand {

    private final Part[] languages;

    public LanguageCommand(){
        super("language", "lang", "sprache");
        this.languages = new Part[]{changeLanguagePart("EN"), changeLanguagePart("DE")};
    }

    @Override
    public void onDefaultExecute(CommandSender sender){
        Messages.create("cmd_not_valid")
                .to(sender)
                .setInjector(AdvancedMessageInjector.class)
                .eachReplace("cmd_usage", languages)
                .handle();
    }

    private Part changeLanguagePart(String language){
        return new Part(language)
                .setHoverAction(HoverAction.SHOW_TEXT, "Change Language to "  + language)
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
