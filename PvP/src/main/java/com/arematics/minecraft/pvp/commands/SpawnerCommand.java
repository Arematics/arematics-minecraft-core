package com.arematics.minecraft.pvp.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.sk89q.worldedit.blocks.metadata.MobType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Perm(permission = "world.interact.blocks.spawner.change", description = "Permission to modify spawning entity inside a spawner")
public class SpawnerCommand extends CoreCommand {

    private static final List<MobType> blockedTypes = new ArrayList<MobType>(){{
        add(MobType.ENDERDRAGON);
        add(MobType.GIANT);
        add(MobType.GHAST);
        add(MobType.WITHER);
        add(MobType.ENDERMAN);
    }};

    public SpawnerCommand(){
        super("spawner", "spawnertype");
    }

    @SubCommand("{type}")
    public void setEntityType(CorePlayer sender, MobType mobType) {
        if(blockedTypes.contains(mobType))
            throw new CommandProcessException("Mob type is not allowed");
        Block block = sender.getPlayer().getTargetBlock((Set<Material>) null, 10);
    }
}
