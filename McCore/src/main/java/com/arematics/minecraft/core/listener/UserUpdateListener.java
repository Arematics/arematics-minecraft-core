package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.chat.ChatAPI;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.scoreboard.functions.BoardHandler;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.tablist.Tablist;
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
import java.util.concurrent.TimeUnit;

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
        Player player = joinEvent.getPlayer();
        dispatchPlayerData(player);
        sendScoreboard(player);
        Timestamp current = new Timestamp(System.currentTimeMillis());
        this.tablist.refresh(CorePlayer.get(player));
        User user = this.userService.getOrCreateUser(player.getUniqueId(), player.getName());
        user.setLastName(player.getName());
        user.setLastIp(Md5Crypt.md5Crypt(player.getAddress().getAddress().getHostAddress().getBytes()));
        user.setLastIpChange(current);
        user.setLastJoin(current);
        chatAPI.login(player);
        chatAPI.getTheme(user.getActiveTheme().getThemeKey()).getActiveUsers().add(user);
        this.userService.update(user);

        Messages.create("broadcast_beta_disclaimer")
                .to(player)
                .DEFAULT()
                .replace("prefix", "§c§lInfo » §7")
                .disableServerPrefix()
                .handle();
    }

    private void sendScoreboard(Player player){
        CorePlayer cp = CorePlayer.get(player);
        final BoardHandler handler = cp.getBoard().getOrAddBoard("main", "§bSoulPvP");
        handler.addEntryData("Kills", "§c", "§7" + cp.getStats().getKills())
                .addEntryData("Deaths", "§c", "§7" + cp.getStats().getDeaths())
                .addEntryData("Coins", "§c", "§7" + cp.getStats().getCoins())
                .show();
    }

    private void dispatchPlayerData(Player player){
        ArematicsExecutor.asyncDelayed(() -> sendInfo(player), 5, TimeUnit.SECONDS);
        sendTab(player);
    }

    private void sendInfo(Player player){
        Messages.create("broadcast_beta_disclaimer")
                .to(player)
                .DEFAULT()
                .replace("prefix", "§c§lInfo » §7")
                .disableServerPrefix()
                .handle();
    }

    private void sendTab(Player player){
        TextComponent header = new TextComponent("Arematics presents SoulPvP");
        header.setColor(ChatColor.AQUA);
        header.setBold(true);
        TextComponent footer = new TextComponent("OPEN BETA");
        footer.setColor(ChatColor.DARK_GRAY);
        footer.setBold(true);
        player.setPlayerListHeaderFooter(header, footer);
    }
}
