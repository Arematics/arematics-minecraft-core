package com.arematics.minecraft.core.command.supplier.page;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.pages.Page;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.Configuration;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

public class PageCommandSupplier implements PageCommandCLI, PageCommandUI, PageCommandAccept{

    public static PageCommandCLI create(Page current){
        return new PageCommandSupplier(current);
    }

    private BiFunction<CorePlayer, Page, Boolean> onCli;
    private BiFunction<CorePlayer, Page, Boolean> onGUI = null;

    private final Page current;
    private final UserService userService;

    private PageCommandSupplier(Page current){
        this.current = current;
        this.userService = Boots.getBoot(CoreBoot.class).getContext().getBean(UserService.class);
    }

    @Override
    public PageCommandUI setCLI(BiFunction<CorePlayer, Page, Boolean> onCli) {
        this.onCli = onCli;
        return this;
    }

    @Override
    public PageCommandAccept setGUI(BiFunction<CorePlayer, Page, Boolean> onUI) {
        this.onGUI = onUI;
        return this;
    }

    @Override
    public boolean accept(CorePlayer player) {
        if(isUIAccepted(player))
            return onGUI.apply(player, current);
        return onCli.apply(player, current);
    }

    private boolean isUIAccepted(CorePlayer player){
        return hasUserUIEnabled(player.getPlayer());
    }

    private boolean hasUserUIEnabled(Player player){
        User user = this.userService.getOrCreateUser(player.getUniqueId(), player.getName());
        return user.getConfigurations().getOrDefault("command-mode", new Configuration(""))
                .getValue().equals("gui");
    }
}
