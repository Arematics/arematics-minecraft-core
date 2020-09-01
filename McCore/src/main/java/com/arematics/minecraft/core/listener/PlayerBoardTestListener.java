package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.annotations.DisableAutoHook;
import com.arematics.minecraft.core.data.model.TestModel;
import com.arematics.minecraft.core.data.service.TestService;
import com.arematics.minecraft.core.scoreboard.functions.BoardHandler;
import com.arematics.minecraft.core.scoreboard.functions.Boards;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

public class PlayerBoardTestListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        ApplicationContext context = Boots.getBoot(CoreBoot.class).getContext();

        TestService service = (TestService) context.getBean("testService");
        TestModel model = service.getModel(1);
        System.out.println(model.getValue());

        Player player = event.getPlayer();
        final BoardHandler handler = Boards.getBoardSet(player).getOrAddBoard("main", "§aSoul");
        handler.addEntryData("Test: §7", "§6", "§4Hallo")
                .show();

        new BukkitRunnable(){
            @Override
            public void run() {
                handler.setEntrySuffix("Test: §7", "§4Allo")
                        .setEntrySuffix("Test: §7", "§5Ein Zebra")
                        .buildEntries();
            }
        }.runTaskLater(Boots.getBoot(CoreBoot.class), 20*5);
    }
}
