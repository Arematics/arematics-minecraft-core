package com.arematics.minecraft.crystals.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.crystals.logic.CrystalKeyItem;
import com.arematics.minecraft.data.mode.model.CrystalKey;
import com.arematics.minecraft.data.service.CrystalKeyService;
import org.bukkit.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "crystal-key", description = "Permission to get a crystal key")
public class CrystalKeyCommand extends CoreCommand {

    private final CrystalKeyService service;

    @Autowired
    public CrystalKeyCommand(CrystalKeyService crystalKeyService) {
        super("crystal-key", "ckey", "ck");
        this.service = crystalKeyService;
    }
    
    @SubCommand("{crystal}")
    @Perm(permission = "item", description = "permission to get a crystal item")
    public void getCrystalKey(CorePlayer player, CrystalKey key) {
        ArematicsExecutor.syncRun(() -> player.getPlayer().getInventory().addItem(CrystalKeyItem.fromKey(key)));
        player.info("You received a crystal key: " + key.getName()).handle();
    }

    @SubCommand("create {name} {colorCode} {r} {g} {b}")
    @Perm(permission = "modify", description = "permission to modify a crystal key and see infos")
    public void createCrystalKey(CorePlayer player, String name, String colorCode, Byte r, Byte g, Byte b) {
        try{
            service.findById(name);
            player.warn("Crystal with name: " + name + " already exists").handle();
        }catch (RuntimeException re){
            CrystalKey crystalKey = new CrystalKey(name, colorCode, Color.fromBGR(r, g, b));
            service.update(crystalKey);
            player.info("Crystal with name: " + name + " has been created").handle();
        }
    }

    @SubCommand("delete {crystal}")
    @Perm(permission = "modify", description = "permission to modify a crystal key and see infos")
    public void deleteCrystalKey(CorePlayer player, CrystalKey key) {
        try{
            service.delete(key);
            player.info("Crystal Key: " + key.getName() + " has been deleted").handle();
        }catch (Exception e){
            player.failure("Crystal Key: " + key.getName() + " could not be deleted").handle();
        }
    }

    @SubCommand("info {crystal}")
    @Perm(permission = "modify", description = "permission to modify a crystal key and see infos")
    public void fetchCrystalInfos(CorePlayer player, CrystalKey key) {

    }
}
