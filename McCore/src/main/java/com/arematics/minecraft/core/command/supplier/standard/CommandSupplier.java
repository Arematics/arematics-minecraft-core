package com.arematics.minecraft.core.command.supplier.standard;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.Configuration;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;

import java.util.function.Consumer;

public class CommandSupplier implements CommandCLI, CommandUI, CommandAccept{

    public static CommandCLI create(){
        return new CommandSupplier();
    }

    private Consumer<CorePlayer> onCli;
    private Consumer<CorePlayer> onGUI = null;

    private final UserService userService;

    private CommandSupplier(){
        this.userService = Boots.getBoot(CoreBoot.class).getContext().getBean(UserService.class);
    }

    @Override
    public CommandUI setCLI(Consumer<CorePlayer> onCli) {
        this.onCli = onCli;
        return this;
    }

    @Override
    public CommandAccept setGUI(Consumer<CorePlayer> onUI) {
        this.onGUI = onUI;
        return this;
    }

    @Override
    public void accept(CorePlayer sender) {
        if(isGUIAccepted(sender))
            onGUI.accept(sender);
        else
            onCli.accept(sender);
    }

    private boolean isGUIAccepted(CorePlayer sender){
        return hasUserGUIEnabled(sender);
    }

    private boolean hasUserGUIEnabled(CorePlayer player){
        User user = player.getUser();
        return user.getConfigurations().getOrDefault("command-mode", new Configuration(""))
                .getValue().equals("gui");
    }
}
