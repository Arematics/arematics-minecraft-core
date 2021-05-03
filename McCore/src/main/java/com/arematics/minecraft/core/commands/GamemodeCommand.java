package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.GameMode;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "world.interact.player.gamemode", description = "Change gamemode")
public class GamemodeCommand extends CoreCommand {

    public GamemodeCommand(){
        super("gm");
    }

    @SubCommand("{mode}")
    public void setMode(CorePlayer player, String mode) {
        changeGameMode(player, player, mode);
    }

    @SubCommand("{mode} {forPlayer}")
    @Perm(permission = "other", description = "Change gamemode for other player")
    public void setModeFor(CorePlayer sender, String mode, CorePlayer forPlayer) {
        changeGameMode(sender, forPlayer, mode);
    }

    private void changeGameMode(CorePlayer player, CorePlayer target, String mode){
        try{
            GameMode modeE = GameMode.getByValue(Integer.parseInt(mode));
            server.schedule().runSync(() -> target.getPlayer().setGameMode(modeE));
            target.info("gamemode")
                    .DEFAULT()
                    .replace("mode", modeE.name())
                    .handle();
        }catch (NumberFormatException nfe){
            GameMode modeE = GameMode.valueOf(mode.toUpperCase());
            server.schedule().runSync(() -> target.getPlayer().setGameMode(modeE));
            target.info("gamemode")
                    .DEFAULT()
                    .replace("mode", modeE.name())
                    .handle();
        }
    }
}
