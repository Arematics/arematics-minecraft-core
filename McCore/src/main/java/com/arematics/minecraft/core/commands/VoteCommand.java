package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.CommandUtils;
import com.arematics.minecraft.data.global.model.PlayerVotes;
import com.arematics.minecraft.data.mode.model.VoteReward;
import com.arematics.minecraft.data.service.InventoryService;
import com.arematics.minecraft.data.service.PlayerVotesService;
import com.arematics.minecraft.data.service.VoteRewardService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Perm(permission = "utils.vote", description = "Vote Command Permission")
public class VoteCommand extends CoreCommand {

    private final PlayerVotesService votesService;
    private final VoteRewardService voteRewardService;
    private final InventoryService inventoryService;

    @Autowired
    public VoteCommand(PlayerVotesService playerVotesService,
                       VoteRewardService voteRewardService,
                       InventoryService inventoryService){
        super("vote", "votes");
        this.votesService = playerVotesService;
        this.voteRewardService = voteRewardService;
        this.inventoryService = inventoryService;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        String header = CommandUtils.prettyHeader("Votes", "Information");
        sender.info(header).DEFAULT().disableServerPrefix().handle();
    }

    @SubCommand("data")
    @Perm(permission = "data", description = "See your vote data")
    public void listOwnVoteData(CorePlayer sender) {
        List<VoteReward> rewards = this.voteRewardService.findAll();
        PlayerVotes votes = this.votesService.getOrCreate(sender.getUUID());
        int size = 18 + 18;
        Inventory inv = Bukkit.createInventory(null, size, "§aVotes");
        inv.setItem(3, generatePlayerInfo(votes));
        sender.inventories().openTotalBlockedInventory(inv);
        int index = 17 + 2;
        for(VoteReward reward : rewards){
            inv.setItem(index, reward.getDisplayItem()[0]);
            index += 2;
        }
    }

    private CoreItem generatePlayerInfo(PlayerVotes playerVotes){
        return CoreItem.generate(Material.BOOK)
                .disableClick()
                .addToLore("    §7> Your Vote Points: §c" + playerVotes.getCurrentVotePoints())
                .addToLore("    §7> Your Streak: §c" + playerVotes.getStreak() + " Days")
                .addToLore("    §7> Total Votes: §c" + playerVotes.getTotalVotes())
                .addToLore("    §7> Free Streak Skips: §c" + playerVotes.getFreeVoteSkips() + " Days")
                .setName("§7Your Vote Data");
    }

    @SubCommand("collect reward {id}")
    public void collectVoteReward(CorePlayer player, String rewardId) {
        VoteReward reward;
        try{
            reward = this.voteRewardService.findVoteReward(rewardId);
        }catch (RuntimeException re){
            throw new CommandProcessException("No vote reward found for this id");
        }
        PlayerVotes votes = this.votesService.getOrCreate(player.getUUID());
        if(reward.getCosts() > votes.getCurrentVotePoints())
            throw new CommandProcessException("You dont have enough vote points");
        Inventory inv;
        try{
            inv = this.inventoryService.getInventory("inventory_vote_reward_" + reward.getId());
        }catch (RuntimeException re){
            player.failure("No reward items found. Please report this").handle();
            return;
        }
        List<CoreItem> itemList = Arrays.stream(inv.getContents())
                .filter(Objects::nonNull)
                .map(CoreItem::create)
                .collect(Collectors.toList());
        player.getPlayer().getInventory().addItem(itemList.toArray(new CoreItem[]{}));
        votes.setCurrentVotePoints(votes.getCurrentVotePoints() - reward.getCosts());
        this.votesService.updatePlayerVotes(votes);
        player.info("You have received your vote reward").handle();
    }
}
