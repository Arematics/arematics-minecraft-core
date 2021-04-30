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
    public void onDefaultExecute(CorePlayer sender) {
        CooldownKey key = new CooldownKey(sender.getUUID().toString(), "rtp");
        if(cooldownService.hasCooldown(key)){
            Cooldown cooldown = cooldownService.getModeCooldown(key).orElse(null);
            throw new CommandProcessException("You need to wait until " + TimeUtils.toString(cooldown.getEndTime()) + " to use rtp");
        }

        int x = random.nextInt(3000) + 2000;
        int z = random.nextInt(3000) + 2000;
        int xOff = random.nextInt(2);
        int zOff = random.nextInt(2);
        x = xOff == 1 ? Math.negateExact(x) : x;
        z = zOff == 1 ? Math.negateExact(z) : z;
        Location location = new Location(sender.getPlayer().getWorld(), x, 100, z);

        ArematicsExecutor.syncRun(() ->{
                    Block block = location.getWorld().getHighestBlockAt(location);
                    sender.interact().teleport(block.getLocation().add(0, 2, 0)).onEnd(c -> {
                                addCooldown(key);
                                sender.info("Random teleport is called").handle(); }).schedule();
                }
        );
    }

    private void addCooldown(CooldownKey key){
        cooldownService.ofMode(new Cooldown(key.getId(), key.getSecondKey(),
                System.currentTimeMillis() + (1000 * 60 * 60 * 48)));
    }
}
