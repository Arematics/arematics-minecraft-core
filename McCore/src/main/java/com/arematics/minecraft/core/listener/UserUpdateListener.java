package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.bukkit.scoreboard.functions.BoardHandler;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.bukkit.Tablist;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.codec.digest.Md5Crypt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class UserUpdateListener implements Listener {

    private final UserService userService;
    private final ChatAPI chatAPI;
    private final Tablist tablist;

    @Autowired
    public UserUpdateListener(UserService userService, ChatAPI chatAPI, Tablist tablist){
        this.userService = userService;
        this.chatAPI = chatAPI;
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
        player.info("broadcast_beta_disclaimer")
                .DEFAULT()
                .replace("prefix", "§c§lInfo » §7")
                .disableServerPrefix()
                .handle();
    }

    private void sendScoreboard(CorePlayer player){
        final BoardHandler handler = player.getBoard().getOrAddBoard("main", "§b§lSOULPVP.DE");
        handler.addEntryData("Coins", "§c", "§7" + player.getStats().getCoins())
                .addEntryData("Deaths", "§c", "§7" + player.getStats().getDeaths())
                .addEntryData("Kills", "§c", "§7" + player.getStats().getKills());

        ArematicsExecutor.syncRun(handler::show);
    }

    private void dispatchPlayerData(CorePlayer player){
        ArematicsExecutor.syncRun(() -> sendTab(player));
        sendInfo(player);
        patchUser(player);
    }

    private void patchUser(CorePlayer player){
        Timestamp current = new Timestamp(System.currentTimeMillis());
        User user = this.userService.getOrCreateUser(player.getUUID(), player.getPlayer().getName());
        chatAPI.login(player);
        chatAPI.getTheme(user.getActiveTheme().getThemeKey()).getActiveUsers().add(player);
        user.setLastName(player.getPlayer().getName());
        user.setLastIp(Md5Crypt.md5Crypt(player.getPlayer().getAddress().getAddress().getHostAddress().getBytes()));
        user.setLastIpChange(current);
        user.setLastJoin(current);
        this.userService.update(user);
    }

    private void sendInfo(CorePlayer player){
        player.info("broadcast_beta_disclaimer")
                .DEFAULT()
                .replace("prefix", "§c§lInfo » §7")
                .disableServerPrefix()
                .handle();
    }

    private void sendTab(CorePlayer player){
        this.tablist.refresh(player);
        TextComponent header = new TextComponent("Arematics presents SoulPvP");
        header.setColor(ChatColor.AQUA);
        header.setBold(true);
        TextComponent footer = new TextComponent("OPEN BETA");
        footer.setColor(ChatColor.DARK_GRAY);
        footer.setBold(true);
        player.getPlayer().setPlayerListHeaderFooter(header, footer);
    }
}
