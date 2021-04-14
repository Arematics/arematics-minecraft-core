package com.arematics.minecraft.pvp.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.messaging.advanced.MSGBuilder;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.sk89q.worldedit.blocks.metadata.MobType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Perm(permission = "world.interact.blocks.spawner.change", description = "Permission to modify spawning entity inside a spawner")
public class SpawnerCommand extends CoreCommand {

    private static final List<MobType> blockedTypes = new ArrayList<MobType>(){{
        add(MobType.ENDERDRAGON);
        add(MobType.GIANT);
        add(MobType.GHAST);
        add(MobType.WITHER);
        add(MobType.ENDERMAN);
        add(MobType.VILLAGER_GOLEM);
        add(MobType.VILLAGER);
    }};

    private final List<MobType> mobTypes;

    public SpawnerCommand(){
        super("spawner", "spawnertype");
        this.mobTypes = Arrays.stream(MobType.values())
                .filter(value -> !blockedTypes.contains(value))
                .collect(Collectors.toList());
    }

    @SubCommand("{type}")
    public void setEntityType(CorePlayer sender, MobType mobType) {
        if(blockedTypes.contains(mobType)) throw new CommandProcessException("Mob type is not allowed");
        Block block = sender.getPlayer().getTargetBlock((Set<Material>) null, 10);
        if(block.getType() == Material.MOB_SPAWNER){
            CreatureSpawner cs = (CreatureSpawner) block.getState();
            EntityType type = EntityType.fromName(mobType.getName());
            cs.setSpawnedType(type);
            cs.update();
            sender.info("Spawner type changed to " + mobType.getName()).handle();
        }
    }

    @SubCommand("list types")
    public void listAllTypes(CorePlayer sender) {
        List<Part> parts = this.mobTypes.stream().map(this::toPart).collect(Collectors.toList());
        sender.info("listing")
                .setInjector(AdvancedMessageInjector.class)
                .replace("list_type", new Part("Mob"))
                .replace("list_value", MSGBuilder.join(parts, ','))
                .handle();
    }

    private Part toPart(MobType type){
        return PartBuilder.createHoverAndSuggest(type.getName(),
                "§aChange spawner to mob type " + type.getName(),
                "spawner " + type.getName());
    }
}
