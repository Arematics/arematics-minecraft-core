package com.arematics.minecraft.core.utils;

import com.arematics.minecraft.core.messaging.advanced.*;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.data.domain.Page;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CommandUtils {

    private static final DecimalFormat FORMATTER = new DecimalFormat("#.##");

    public static String prettyReplace(String key, String keyValue){
        return "§a\n\n§7" + key + " » " + "§c" + keyValue + "\n" + "%value%";
    }

    public static String prettyHeader(String key, String value){
        return "§a\n\n§7" + key + " » " + "§c" + value;
    }

    public static String prettyBoolean(boolean value){
        return value ? "Yes" : "No";
    }

    public static void sendPreviousAndNext(CorePlayer sender, String cmd, boolean hasPrevious, boolean hasNext){
        Part previousPart = hasPrevious ? new Part("PREVIOUS")
                .setHoverAction(HoverAction.SHOW_TEXT, "§aPage before")
                .setClickAction(ClickAction.RUN_COMMAND, "/page BEFORE " + cmd) : new Part("");
        Part nextPart = hasNext ? new Part("NEXT")
                .setHoverAction(HoverAction.SHOW_TEXT, "§aNext Page")
                .setClickAction(ClickAction.RUN_COMMAND, "/page NEXT " + cmd) : new Part("");
        sender.info("&8« &a%previous% &8&l| &a%next% &8»")
                .setInjector(AdvancedMessageInjector.class)
                .disableServerPrefix()
                .replace("previous", previousPart)
                .replace("next", nextPart)
                .handle();
    }

    public static <T> void sendPagingList(CorePlayer sender,
                                                                      Supplier<Page<T>> paging,
                                                                      Function<T, MSG> messageMapper,
                                                                      String type,
                                                                      String cmd){
        Page<T> result = paging.get();
        if(result.isEmpty() && result.isLast() && result.hasPrevious()) sender.inventories().resetPages();
        List<MSG> msgs = result.getContent().stream().map(messageMapper).collect(Collectors.toList());
        sender.info("listing")
                .setInjector(AdvancedMessageInjector.class)
                .replace("list_type", new Part(type))
                .replace("list_value", MSGBuilder.joinMessages(msgs, ','))
                .handle();
        if(result.hasNext() || result.hasPrevious())
            CommandUtils.sendPreviousAndNext(sender, cmd, result.hasPrevious(), result.hasNext());
    }

    public static String prettyDecimal(double value){
        return FORMATTER.format(value);
    }
}
