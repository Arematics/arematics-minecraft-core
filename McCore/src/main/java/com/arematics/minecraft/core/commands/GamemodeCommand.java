package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import org.bukkit.GameMode;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "world.interact.player.gamemode", description = "Change gamemode")
public class GamemodeCommand extends CoreCommand {

    public GamemodeCommand(){
        super("gm");
    }

    @SubCommand("{mode}")
    public boolean setMode(CorePlayer player, String mode) {
        ArematicsExecutor.syncRun(() -> runSync(player, player, mode));
        return true;
    }

    @SubCommand("{mode} {forPlayer}")
    @Perm(permission = "other", description = "Change gamemode for other player")
    public void setModeFor(CorePlayer sender, String mode, CorePlayer forPlayer) {
        ArematicsExecutor.syncRun(() -> runSync(sender, forPlayer, mode));
    }

    private void runSync(CorePlayer player, CorePlayer target, String mode){
        try{
            GameMode modeE = GameMode.getByValue(Integer.parseInt(mode));
            target.getPlayer().setGameMode(modeE);
            target.info("gamemode")
                    .DEFAULT()
                    .replace("mode", modeE.name())
                    .handle();
        }catch (NumberFormatException nfe){
            GameMode modeE = GameMode.valueOf(mode.toUpperCase());
            target.getPlayer().setGameMode(modeE);
            target.info("gamemode")
                    .DEFAULT()
                    .replace("mode", modeE.name())
                    .handle();
        }
    }
}
