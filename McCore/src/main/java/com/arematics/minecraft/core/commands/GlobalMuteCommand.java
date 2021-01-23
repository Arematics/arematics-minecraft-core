package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

@Component
@Getter
@Perm(permission = "globalmute", description = "mute the global chat")
public class GlobalMuteCommand extends CoreCommand {

    private boolean isGlobalMuteActive = false;

    public GlobalMuteCommand() { super("globalmute", "glm"); }

    @Override
    protected boolean onDefaultCLI(CommandSender sender) {
        enableGlobalMute(sender);
        return true;
    }

    @SubCommand("info")
    public void getInfo(CorePlayer player) {
        player.info("Globalmute: " + isGlobalMuteActive).handle();
    }

    private void enableGlobalMute(CommandSender sender) {
        isGlobalMuteActive = !isGlobalMuteActive;
        String globalMuteStatus = isGlobalMuteActive ? "muted" : "demuted";

        Bukkit.getOnlinePlayers().stream()
                .map(CorePlayer::get)
                .forEach(player -> player
                        .info("The chat was " + globalMuteStatus +" by " + ((Player) sender).getDisplayName()).handle());

    }

    public boolean getGlobalMuteStatus() {
        return isGlobalMuteActive;
    }
}
