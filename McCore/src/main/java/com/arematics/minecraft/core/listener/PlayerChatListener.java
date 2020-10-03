package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.chat.model.ChatClickAction;
import com.arematics.minecraft.core.chat.model.ChatHoverAction;
import com.arematics.minecraft.core.chat.model.ChatThemeUser;
import com.arematics.minecraft.core.chat.model.ReplacementData;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent chatEvent) {
        ChatAPI.registerPlaceholder("rank", false);
        ChatAPI.registerPlaceholder("clan", false);
        ChatAPI.registerPlaceholder("name", false);
        ChatAPI.registerPlaceholder("chatMessage", false);
        //TODO delegate logic and implement chatthemes this is testing
        Player player = chatEvent.getPlayer();
        ChatAPI.updatePlaceholder("rank", "Developer", player);
        ChatAPI.updatePlaceholder("name", player.getDisplayName(), player);
        ChatAPI.updatePlaceholder("clan", "ZeRo", player);
        ChatAPI.updatePlaceholder("chatMessage", chatEvent.getMessage(), player);
        CommandSender[] senders = chatEvent.getRecipients().toArray(new CommandSender[0]);
        // "" "Dev" ": " "ZeRo" "*" "McMeze" "> " Hallo guys
        ChatThemeUser chatThemeUser = ChatAPI.getThemeUser(player);
        List<ReplacementData> placeholders = new ArrayList<>();
        String[] toReplace = chatThemeUser.getActiveTheme().getPlaceholders();
        for (int i = 0; i < toReplace.length; i++) {
            String placeholder = toReplace[i];
            String replacement = ChatAPI.getPlaceholder(placeholder).getPlaceholderValues().get(player);
            ReplacementData replacementData = new ReplacementData();
            replacementData.setKey(placeholder);
            replacementData.setValue(replacement);
            replacementData.getHover().add(new ChatHoverAction(HoverAction.SHOW_TEXT, "placeholderAPI: " + placeholder + "to" + replacement));
            replacementData.getClick().add(new ChatClickAction(ClickAction.OPEN_URL, "youtube.com"));
            placeholders.add(replacementData);
        }


        Messages.create(chatThemeUser.getActiveTheme().getFormat()).to(senders).setInjector(AdvancedMessageInjector.class).replaceAllChatPlaceholders(placeholders).END().handle();
        chatEvent.setCancelled(true);
    }

}
