package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.springframework.stereotype.Component;

@Component
@Getter
@Perm(permission = "team.punishment.chat.globalmute", description = "mute the global chat")
public class GlobalMuteCommand extends CoreCommand {

    private boolean isGlobalMuteActive = false;

    public GlobalMuteCommand() { super("globalmute", "glm"); }

    @Override
    protected void onDefaultCLI(CorePlayer sender) {
        enableGlobalMute(sender);
    }

    @SubCommand("info")
    public void getInfo(CorePlayer player) {
        player.info("Globalmute: " + isGlobalMuteActive).handle();
    }

    private void enableGlobalMute(CorePlayer sender) {
        isGlobalMuteActive = !isGlobalMuteActive;
        String globalMuteStatus = isGlobalMuteActive ? "muted" : "unmuted";

        Messages.create("chat_mute")
                .to(Bukkit.getOnlinePlayers().toArray(new CommandSender[]{}))
                .DEFAULT()
                .replace("name", sender.getName())
                .replace("value", globalMuteStatus)
                .handle();
    }

    public boolean getGlobalMuteStatus() {
        return isGlobalMuteActive;
    }
}
