package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.CorePlayer;
import org.springframework.stereotype.Component;

@Component
//@Perm(permission = "msg", description = "send a message")
public class MessageCommand extends CoreCommand {

    public MessageCommand() {
        super("msg", "tell", "whisper", "w", "message");
        registerLongArgument("args");
    }

    @SubCommand("{player} {args}")
    public void message(CorePlayer player, CorePlayer target, String args) {
       sendMsg(player, target, args);
    }

    public void sendMsg(CorePlayer player, CorePlayer target, String args) {
        if(player == target) {
            return;
        }
        player.info("§8<§bMSG§8> §7Du §8» §b" + target.getPlayer().getDisplayName() + "§7: §f" + args).DEFAULT().disableServerPrefix().handle();
        target.info("§8<§bMSG§8> §b" + player.getPlayer().getDisplayName() + " §8» §7Dir: §f" + args).DEFAULT().disableServerPrefix().handle();
    }

    private String buildMsg(String[] input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length; i++) {
            sb.append(input[i]);
            if (i < input.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString().replace("&", "§");
    }


}
