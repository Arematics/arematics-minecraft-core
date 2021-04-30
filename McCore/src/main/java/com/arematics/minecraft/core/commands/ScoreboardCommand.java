package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.bukkit.scoreboard.functions.BoardHandler;
import com.arematics.minecraft.core.bukkit.scoreboard.model.Board;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

@Component
public class ScoreboardCommand extends CoreCommand {

    public ScoreboardCommand(){
        super("scoreboard", true, "sb", "board");
    }

    @SubCommand("mode toggle")
    public void toggleScoreboardMode(CorePlayer sender){
        Board current = sender.getBoard().getShown();
        if(current != null){
            BoardHandler handler = sender.getBoard().getBoard(current.BOARD_ID);
            handler.toggleModernBoard();
            sender.info("Your scoreboard mode has been toggled").handle();
        }else{
            sender.warn("No current scoreboard could be found").handle();
        }
    }
}
