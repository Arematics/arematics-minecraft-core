package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "clear", description = "clear the Chat")
public class ChatClearCommand extends CoreCommand  {

    public ChatClearCommand() {
        super("cc");
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {

        for(int i = 0; i < 120; i++) Bukkit.broadcastMessage(" ");

        Bukkit.getOnlinePlayers().stream()
                .map(CorePlayer::get)
                .forEach(player -> player.info("Chat cleared from " + sender.getName()).handle());
    }
}
