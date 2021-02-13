package com.arematics.minecraft.core.utils;

import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;

public class CommandUtils {

    public static String prettyReplace(String key, String keyValue){
        return "§a\n\n§7" + key + " » " + "§c" + keyValue + "\n" + "%value%";
    }

    public static String prettyHeader(String key, String value){
        return "§a\n\n§7" + key + " » " + "§c" + value;
    }

    public static String prettyBoolean(boolean value){
        return value ? "Yes" : "No";
    }

    public static void sendPreviousAndNext(CorePlayer sender, String previous, String next){
        sender.info("&8« &a%previous% &8&l| &a%next% &8»")
                .setInjector(AdvancedMessageInjector.class)
                .disableServerPrefix()
                .replace("previous", new Part("PREVIOUS")
                        .setHoverAction(HoverAction.SHOW_TEXT, "§aPage before")
                        .setClickAction(ClickAction.RUN_COMMAND, "/" + previous))
                .replace("next", new Part("NEXT")
                        .setHoverAction(HoverAction.SHOW_TEXT, "§aNext Page")
                        .setClickAction(ClickAction.RUN_COMMAND, "/" + next))
                .handle();
    }
}
