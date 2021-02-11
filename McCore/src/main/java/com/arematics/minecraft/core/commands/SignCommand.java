package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Perm(permission = "world.interact.sign", description = "Permission to write signs with color")
public class SignCommand extends CoreCommand {

    public SignCommand(){
        super("sign");
        registerLongArgument("text");
    }

    @SubCommand("set {line} {text}")
    public void setSignText(CorePlayer player, Byte line, String text) {
        text = text.replaceAll("&", "§");
        Block block = player.getPlayer().getTargetBlock((Set<Material>) null, 4);
        if(block.getType() != Material.SIGN && block.getType() != Material.WALL_SIGN)
            throw new CommandProcessException("You're not facing a sign");
        if(line > 4 || line < 1)
            throw new CommandProcessException("Line size for sign is 1-4");
        Sign sign = (Sign) block.getState();
        sign.setLine(line - 1, text);
        sign.update();
        player.info("Sign line changed").handle();
    }
}
