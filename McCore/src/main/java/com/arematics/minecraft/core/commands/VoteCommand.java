package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.CommandUtils;
import com.arematics.minecraft.data.mode.model.VoteReward;
import com.arematics.minecraft.data.service.PlayerVotesService;
import com.arematics.minecraft.data.service.VoteRewardService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Perm(permission = "utils.vote", description = "Vote Command Permission")
public class VoteCommand extends CoreCommand {

    private final PlayerVotesService votesService;
    private final VoteRewardService voteRewardService;

    @Autowired
    public VoteCommand(PlayerVotesService playerVotesService,
                       VoteRewardService voteRewardService){
        super("vote", "votes");
        this.votesService = playerVotesService;
        this.voteRewardService = voteRewardService;
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {
        if(!(sender instanceof Player)) throw new CommandProcessException("This command is only for players");
        CorePlayer player = CorePlayer.get((Player) sender);
        String header = CommandUtils.prettyHeader("Votes", "Information");
        player.info(header).DEFAULT().disableServerPrefix().handle();
    }

    @SubCommand("data")
    @Perm(permission = "data", description = "See your vote data")
    public void listOwnVoteData(CorePlayer sender) {
        List<VoteReward> rewards = this.voteRewardService.findAll();
        int size = 18 + 18;
        Inventory inv = Bukkit.createInventory(null, size, "§aVotes");
        sender.openTotalBlockedInventory(inv);
        int index = 17 + 2;
        for(VoteReward reward : rewards){
            inv.setItem(index, reward.getDisplayItem()[0]);
            index += 2;
        }
    }
}
