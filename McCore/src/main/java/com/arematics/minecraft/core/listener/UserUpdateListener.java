package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.bukkit.Tablist;
import com.arematics.minecraft.core.bukkit.scoreboard.functions.BoardHandler;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class UserUpdateListener implements Listener {

    private final UserService userService;
    private final Tablist tablist;

    @Autowired
    public UserUpdateListener(UserService userService, Tablist tablist){
        this.userService = userService;
        this.tablist = tablist;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent joinEvent){
        this.asyncedJoin(joinEvent.getPlayer());
    }

    private void asyncedJoin(Player joinPlayer){
        CorePlayer player = CorePlayer.get(joinPlayer);
        dispatchPlayerData(player);
        sendScoreboard(player);
    }

    private void sendScoreboard(CorePlayer player){
        final BoardHandler handler = player.getBoard().getOrAddBoard("main", "§b§lSOULPVP.DE");
        handler.addEntryData("Coins", "§c", "§7" + player.stripMoney())
                .addEntryData("Deaths", "§c", "§7" + player.getStats().getDeaths())
                .addEntryData("Kills", "§c", "§7" + player.getStats().getKills());

        ArematicsExecutor.syncRun(handler::show);
    }

    private void dispatchPlayerData(CorePlayer player){
        patchUser(player);
        ArematicsExecutor.syncRun(() -> sendTab(player));
    }

    private void patchUser(CorePlayer player){
        Timestamp current = Timestamp.valueOf(LocalDateTime.now());
        User user = this.userService.getOrCreateUser(player.getUUID(), player.getPlayer().getName());
        user.setLastName(player.getPlayer().getName());
        user.setLastJoin(current);
        this.userService.update(user);
    }

    private void sendTab(CorePlayer player){
        this.tablist.refresh(player);
        TextComponent header = new TextComponent("Arematics presents SoulPvP");
        header.setColor(ChatColor.AQUA);
        header.setBold(true);
        TextComponent footer = new TextComponent("CLOSED BETA");
        footer.setColor(ChatColor.DARK_GRAY);
        footer.setBold(true);
        player.getPlayer().setPlayerListHeaderFooter(header, footer);
    }
}
