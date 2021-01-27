package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.VoteReward;
import com.arematics.minecraft.data.service.InventoryService;
import com.arematics.minecraft.data.service.VoteRewardService;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "votes.administration")
public class VoteAdminCommand extends CoreCommand {

    private final VoteRewardService service;
    private final InventoryService inventoryService;

    @Autowired
    public VoteAdminCommand(VoteRewardService voteRewardService,
                            InventoryService inventoryService){
        super("vadmin", "votes-admin");
        this.service = voteRewardService;
        this.inventoryService = inventoryService;
    }

    @SubCommand("create reward {id} {costs}")
    public void createVoteReward(CorePlayer player, String id, Integer costs) {
        CoreItem item = player.getItemInHand();
        if(item == null){
            player.warn("no_item_in_hand").handle();
            return;
        }
        try{
            this.service.findVoteReward(id);
            player.warn("Vote Reward with id: " + id + " already exists").handle();
        }catch (RuntimeException re){
            VoteReward voteReward = new VoteReward(id, costs, new CoreItem[]{item});
            this.service.update(voteReward);
            player.info("New Vote Reward with id: " + id + " has been created").handle();
        }
    }

    @SubCommand("setCosts {id} {costs}")
    public void setVoteRewardCosts(CorePlayer player, String id, Integer costs) {
        try{
            VoteReward reward = this.service.findVoteReward(id);
            reward.setCosts(costs);
            this.service.update(reward);
            player.info("Vote Reward Costs changed to " + costs + " Vote Points").handle();
        }catch (RuntimeException re){
            player.warn("Vote Reward with id: " + id + " not exists").handle();
        }
    }

    @SubCommand("delete {id}")
    public void deleteVoteReward(CorePlayer player, String id) {
        try{
            VoteReward reward = this.service.findVoteReward(id);
            this.service.deleteReward(reward);
            this.inventoryService.delete("inventory_vote_reward_" + id);
            player.info("Vote Reward with id: " + id + " has been deleted").handle();
        }catch (RuntimeException re){
            player.warn("Vote Reward with id: " + id + " not exists").handle();
        }
    }

    @SubCommand("edit {id}")
    public void editRewardsForVoteReward(CorePlayer player, String id) {
        try{
            VoteReward reward = this.service.findVoteReward(id);
            this.service.deleteReward(reward);
            Inventory inv = this.inventoryService.getOrCreate("inventory_vote_reward_" + id, "§aReward: " + id, (byte) 18);
            player.openLowerEnabledInventory(inv);
        }catch (RuntimeException re){
            player.warn("Vote Reward with id: " + id + " not exists").handle();
        }
    }
}
