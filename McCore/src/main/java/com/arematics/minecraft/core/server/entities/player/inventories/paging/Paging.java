package com.arematics.minecraft.core.server.entities.player.inventories.paging;

import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.CommandUtils;
import com.arematics.minecraft.data.global.model.BukkitMessageMapper;
import com.arematics.minecraft.data.global.model.Configuration;
import com.arematics.minecraft.data.global.model.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Paging<T> implements PagingCLI<T>, PagingEasyCLI<T>, PagingGUI<T>, PagingEnd {

    public static <T> PagingCLI<T> create(CorePlayer player, Supplier<Page<T>> pager){
        return new Paging<>(player, pager);
    }

    public static <T extends BukkitMessageMapper> PagingEasyCLI<T> createWithMapper(CorePlayer player, Supplier<Page<T>> pager){
        Paging<T> paging = new Paging<>(player, pager);
        paging.messageMapper = T::mapToMessage;
        return paging;
    }

    private final CorePlayer player;
    private final Supplier<Page<T>> pager;
    private Function<T, MSG> messageMapper;
    private String type;
    private String clickCmd;
    private BiConsumer<CorePlayer, Supplier<Page<T>>> consumer;

    @Override
    public PagingGUI<T> onCLI(String type, String clickCmd) {
        this.type = type;
        this.clickCmd = clickCmd;
        return this;
    }

    @Override
    public PagingGUI<T> onCLI(Function<T, MSG> messageMapper, String type, String clickCmd) {
        this.messageMapper = messageMapper;
        this.type = type;
        this.clickCmd = clickCmd;
        return this;
    }

    @Override
    public void execute() {
        if(isGUIAccepted(player))
            consumer.accept(player, pager);
        else
            CommandUtils.sendPagingList(player, pager, messageMapper, type, clickCmd);
    }

    private boolean isGUIAccepted(CorePlayer sender){
        return hasUserGUIEnabled(sender);
    }

    private boolean hasUserGUIEnabled(CorePlayer player){
        User user = player.getUser();
        return user.getConfigurations().getOrDefault("command-mode", new Configuration(""))
                .getValue().equals("gui");
    }

    @Override
    public PagingEnd onGUI(BiConsumer<CorePlayer, Supplier<Page<T>>> consumer) {
        this.consumer = consumer;
        return this;
    }
}
