package com.arematics.minecraft.crystals.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.crystals.logic.CrystalKeyItem;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "crystal-key", description = "Permission to get a crystal key")
public class CrystalKeyCommand extends CoreCommand {


    public CrystalKeyCommand() {
        super("crystal-key", "ckey", "ck");
    }
    
    @SubCommand("{crystal}")
    public void getCrystalKey(CorePlayer player, CrystalKey key) {
        ArematicsExecutor.syncRun(() -> player.getPlayer().getInventory().addItem(CrystalKeyItem.fromKey(key)));
        player.info("You received a crystal key: " + key.getName()).handle();
    }

    @SubCommand("info {crystal}")
    @Perm(permission = "modify", description = "permission to modify a crystal key and see infos")
    public void fetchCrystalInfos(CorePlayer player, CrystalKey key) {

    }
}
