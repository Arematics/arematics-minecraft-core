package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Random;

@Component
@Perm(permission = "random", description = "get a random player")
public class RandomCommand extends CoreCommand {
    public RandomCommand() {
        super("random", "rnd");
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {
        ArrayList<CorePlayer> listOfPlayer = new ArrayList<>();

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!CorePlayer.get(player).getUser().getRank().isInTeam())
                listOfPlayer.add(CorePlayer.get(player));
        }

        if(listOfPlayer.size() > 0)
        Bukkit.getOnlinePlayers().stream()
                .map(CorePlayer::get)
                .forEach(player ->
                        player.info(listOfPlayer.get(new Random().nextInt(listOfPlayer.size())).getPlayer().getDisplayName()
                                +" was randomly selected").handle());
    }

}
