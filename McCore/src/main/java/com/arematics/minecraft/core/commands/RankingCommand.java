package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageReplace;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.CommandUtils;
import com.arematics.minecraft.data.mode.model.GameStats;
import com.arematics.minecraft.data.service.GameStatsService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class RankingCommand extends CoreCommand {

    private final GameStatsService gameStatsService;

    @Autowired
    public RankingCommand(GameStatsService gameStatsService){
        super("ranking", true, "top");
        this.gameStatsService = gameStatsService;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        super.onDefaultExecute(sender);
    }

    @SubCommand("money")
    public void sendMoneyRanking(CorePlayer sender) {
        sendGameStatsRanking(sender, "Money", (stats) -> CommandUtils.prettyDecimal(stats.getCoins()) + " Coins",
                Sort.sort(GameStats.class).by(GameStats::getCoins).descending());
    }

    @SubCommand("kills")
    public void sendKillsRanking(CorePlayer sender) {
        sendGameStatsRanking(sender, "Kill", (stats) -> stats.getKills() + " Kills",
                Sort.sort(GameStats.class).by(GameStats::getKills).descending());
    }

    @SubCommand("deaths")
    public void sendDeathsRanking(CorePlayer sender) {
        sendGameStatsRanking(sender, "Money", (stats) -> stats.getDeaths() + " Deaths",
                Sort.sort(GameStats.class).by(GameStats::getDeaths).descending());
    }

    @SubCommand("onlinetime")
    public void sendOnlineTimeRanking(CorePlayer sender) {
    }

    @SubCommand("streak")
    public void sendKillStreakRanking(CorePlayer sender) {
    }

    @SubCommand("clans")
    public void sendClanRanking(CorePlayer sender) {
    }

    @SubCommand("votes")
    public void sendVoteStreakRanking(CorePlayer sender) {
    }

    private void sendGameStatsRanking(CorePlayer sender, String key, Function<GameStats, String> valueMapper, Sort sort){
        List<Part> parts = gameStatsService.findTopTenBy(sort).stream()
                .map(stats -> toPart(stats, valueMapper))
                .collect(Collectors.toList());
        while(parts.size() < 10) parts.add(new Part("Not found"));
        sendHeader(sender, key);
        AdvancedMessageReplace replacer = sender.info(fetchTop10Message()).setInjector(AdvancedMessageInjector.class)
                .disableServerPrefix();
        IntStream.rangeClosed(0, 9)
                .forEach(index -> replacer.replace("place_" + (index + 1), parts.get(index)));
        replacer.handle();
    }

    private Part toPart(GameStats gameStats, Function<GameStats, String> valueMapper){
        OfflinePlayer player = Bukkit.getOfflinePlayer(gameStats.getUuid());
        return PartBuilder.createHoverAndSuggest(player.getName() + " - " + valueMapper.apply(gameStats),
                "§aWatch " + player.getName() + "'s stats",
                "stats " + player.getName());
    }

    private void sendHeader(CorePlayer player, String key){
        player.info(CommandUtils.prettyHeader("Ranking", key)).DEFAULT().disableServerPrefix().handle();
    }

    private String fetchTop10Message(){
        return "&a    §61. §7%place_1%" + "\n" +
                "   §c2. §7%place_2%" + "\n" +
                "   §83. §7%place_3%" + "\n" +
                "   §74. %place_4%" + "\n" +
                "   §75. %place_5%" + "\n" +
                "   §76. %place_6%" + "\n" +
                "   §77. %place_7%" + "\n" +
                "   §78. %place_8%" + "\n" +
                "   §79. %place_9%" + "\n" +
                "   §710. %place_10%";
    }
}
