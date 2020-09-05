package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.data.model.ArematicsAccount;
import com.arematics.minecraft.core.data.service.ArematicsAccountService;
import com.arematics.minecraft.core.scoreboard.functions.BoardHandler;
import com.arematics.minecraft.core.scoreboard.functions.Boards;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerBoardTestListener implements Listener{

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        ArematicsAccountService accountService = (ArematicsAccountService) Boots.getBoot(CoreBoot.class)
                .getContext().getBean("arematicsAccountService");
        ArematicsAccount account = accountService.findOrCreateBySoulConnection(event.getPlayer().getUniqueId());
        System.out.println(account);
        event.getPlayer().getInventory().addItem(account.getTest()[0].getItem());

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
