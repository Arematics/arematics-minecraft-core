package com.arematics.minecraft.core.command;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.data.global.model.Configuration;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class CommandSupplier implements CommandCLI, CommandUI, CommandAccept{

    public static CommandCLI create(){
        return new CommandSupplier();
    }

    private Function<CommandSender, Boolean> onCli;
    private Function<CommandSender, Boolean> onUI = null;

    private final UserService userService;

    private CommandSupplier(){
        this.userService = Boots.getBoot(CoreBoot.class).getContext().getBean(UserService.class);
    }

    @Override
    public CommandUI setCLI(Function<CommandSender, Boolean> onCli) {
        this.onCli = onCli;
        return this;
    }

    @Override
    public CommandAccept setUI(Function<CommandSender, Boolean> onUI) {
        this.onUI = onUI;
        return this;
    }

    @Override
    public boolean accept(CommandSender sender) {
        if(isUIAccepted(sender))
            return onUI.apply(sender);
        return onCli.apply(sender);
    }

    private boolean isUIAccepted(CommandSender sender){
        if(!(sender instanceof Player)) return false;
        return hasUserUIEnabled((Player) sender);
    }

    private boolean hasUserUIEnabled(Player player){
        User user = this.userService.getOrCreateUser(player.getUniqueId(), player.getName());
        return user.getConfigurations().getOrDefault("command-mode", new Configuration(""))
                .getValue().equals("ui");
    }
}
