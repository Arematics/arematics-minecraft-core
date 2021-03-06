package com.arematics.minecraft.farmworld.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Material;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "management.spawnerwrench", description = "Permission to get an spawner wrench")
public class SpawnerWrenchCommand extends CoreCommand {

    public static final String WRENCH_META_KEY = "SPAWNER_WRENCH_2241";

    public SpawnerWrenchCommand(){
        super("wrench");
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        server.items().giveItemTo(sender, getWrench());
        sender.info("Got spawner wrench").handle();
    }

    public CoreItem getWrench(){
        return server.items().generate(Material.GOLD_HOE)
                .setString("wrench", WRENCH_META_KEY)
                .setName("§6Spawner Wrench")
                .addToLore("§8Click on a spawner in the world to collect the spawner");
    }
}
