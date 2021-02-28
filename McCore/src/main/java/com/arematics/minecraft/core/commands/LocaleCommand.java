package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.messaging.advanced.MSGBuilder;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.command.CommandSender;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class LocaleCommand extends CoreCommand {

    public LocaleCommand(){
        super("locale");
    }

    @Override
    protected boolean onDefaultCLI(CommandSender sender) {
        return super.onDefaultCLI(sender);
    }

    @Override
    protected boolean onDefaultGUI(CorePlayer player) {
        return super.onDefaultGUI(player);
    }

    @SubCommand("set {locale}")
    public void setLocale(CorePlayer sender, String locale) {
        Locale[] locales = DateFormat.getAvailableLocales();
        Locale result = Locale.forLanguageTag(locale);
        if(!Arrays.asList(locales).contains(result)) throw new CommandProcessException("Locale " + locale + " not available");
        sender.setLocale(result);
        sender.info("Your locale has been changed").handle();
    }

    @SubCommand("list")
    public void listAllLocales(CorePlayer sender) {
        Locale[] locales = DateFormat.getAvailableLocales();
        List<Part> parts = Arrays.stream(locales).map(this::localeSelect).collect(Collectors.toList());
        sender.info("listing")
                .setInjector(AdvancedMessageInjector.class)
                .replace("list_type", new Part("Home"))
                .replace("list_value", MSGBuilder.join(parts, ','))
                .handle();
    }

    public Part localeSelect(Locale locale){
        String tag = locale.toLanguageTag();
        return PartBuilder.createHoverAndRun(tag, "§aChange locale to " + tag, "/locale set " + tag);
    }
}
