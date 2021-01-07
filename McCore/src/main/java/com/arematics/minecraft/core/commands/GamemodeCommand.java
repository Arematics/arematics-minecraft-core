package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import org.bukkit.GameMode;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "gamemode", description = "Changing your game mode")
public class GamemodeCommand extends CoreCommand {

    public GamemodeCommand(){
        super("gm");
    }

    @SubCommand("{mode}")
    public boolean setMode(CorePlayer player, String mode) {
        ArematicsExecutor.syncRun(() -> runSync(player, mode));
        return true;
    }

    private void runSync(CorePlayer player, String mode){
        try{
            GameMode modeE = GameMode.getByValue(Integer.parseInt(mode));
            player.getPlayer().setGameMode(modeE);
            player.info("gamemode")
                    .DEFAULT()
                    .replace("mode", modeE.name())
                    .handle();
        }catch (NumberFormatException nfe){
            GameMode modeE = GameMode.valueOf(mode.toUpperCase());
            player.getPlayer().setGameMode(modeE);
            player.info("gamemode")
                    .DEFAULT()
                    .replace("mode", modeE.name())
                    .handle();
        }
    }
}
