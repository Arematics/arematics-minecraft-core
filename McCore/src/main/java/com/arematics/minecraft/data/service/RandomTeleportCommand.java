package com.arematics.minecraft.data.service;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.share.model.Cooldown;
import com.arematics.minecraft.data.share.model.CooldownKey;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Perm(permission = "world.interact.teleport.random", description = "Ability to use random teleport")
public class RandomTeleportCommand extends CoreCommand {
    private final CooldownService cooldownService;
    private final Random random = new Random();

    @Autowired
    public RandomTeleportCommand(CooldownService cooldownService){
        super("rtp", "randomteleport");
        this.cooldownService = cooldownService;
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {
        if(!(sender instanceof Player)) return;
        CorePlayer player = CorePlayer.get((Player) sender);
        CooldownKey key = new CooldownKey(player.getUUID().toString(), "rtp");
        if(cooldownService.hasCooldown(key)){
            Cooldown cooldown = cooldownService.getModeCooldown(key).orElse(null);
            throw new CommandProcessException("You need to wait until " + TimeUtils.toString(cooldown.getEndTime()) + " to use rtp");
        }

        ArematicsExecutor.syncRun(() -> {
            Location location = new Location(player.getPlayer().getWorld(), (random.nextInt(5000) - random.nextInt(6000)) + 1000, 100, random.nextInt(5000) - random.nextInt(6000));
            Block block = location.getWorld().getHighestBlockAt(location);
            player.teleport(block.getLocation().add(0, 2, 0)).onEnd(c -> addCooldown(key)).schedule();
        });
        player.info("Random teleport is called").handle();
    }

    private void addCooldown(CooldownKey key){
        cooldownService.ofMode(new Cooldown(key.getId(), key.getSecondKey(),
                System.currentTimeMillis() + (1000 * 60 * 60 * 48)));
    }
}
