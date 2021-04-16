package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.CommandUtils;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.mode.model.GameStats;
import com.arematics.minecraft.data.service.GameStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatsCommand extends CoreCommand {

    private final GameStatsService gameStatsService;

    @Autowired
    public StatsCommand(GameStatsService gameStatsService){
        super("stats");
        this.gameStatsService = gameStatsService;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        seePlayerStats(sender, sender.getUser());
    }

    @SubCommand("{player}")
    public void seePlayerStats(CorePlayer sender, User target) {
        String prettyReplace = CommandUtils.prettyReplace("Stats", target.getLastName());
        sender.info(prettyReplace)
                .DEFAULT()
                .replace("value", toStats(gameStatsService.findGameStats(target.getUuid())))
                .disableServerPrefix()
                .handle();
    }

    public String toStats(GameStats gameStats){
        double kd = gameStats.getKills() == 0 && gameStats.getDeaths() == 0 ? 0 :
                (double) gameStats.getKills() / (double)gameStats.getDeaths();
        return  "   §7Kills: §c" + gameStats.getKills() + "\n" +
                "   §7Deaths: §c" + gameStats.getDeaths() + "\n" +
                "   §7KD: §c" + CommandUtils.prettyDecimal(kd) + "\n" +
                "   §7Coins: §c" + CommandUtils.prettyDecimal(gameStats.getCoins());
    }
}
