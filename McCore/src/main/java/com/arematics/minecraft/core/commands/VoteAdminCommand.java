package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.WrappedInventory;
import com.arematics.minecraft.data.mode.model.VoteReward;
import com.arematics.minecraft.data.service.InventoryService;
import com.arematics.minecraft.data.service.ItemCollectionService;
import com.arematics.minecraft.data.service.VoteRewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Perm(permission = "management.votes")
public class VoteAdminCommand extends CoreCommand {

    private final VoteRewardService service;
    private final InventoryService inventoryService;
    private final ItemCollectionService itemCollectionService;

    @Autowired
    public VoteAdminCommand(VoteRewardService voteRewardService,
                            InventoryService inventoryService,
                            ItemCollectionService itemCollectionService){
        super("vadmin", "votes-admin");
        this.service = voteRewardService;
        this.inventoryService = inventoryService;
        this.itemCollectionService = itemCollectionService;
    }

    @SubCommand("create reward {id} {costs}")
    public void createVoteReward(CorePlayer player, String id, Integer costs) {
        CoreItem item = player.interact().getItemInHand();
        if(item == null){
            player.warn("no_item_in_hand").handle();
            return;
        }
        try{
            this.service.findVoteReward(id);
            player.warn("Vote Reward with id: " + id + " already exists").handle();
        }catch (RuntimeException re){
            VoteReward voteReward = new VoteReward(id, costs, preparedItem(id, costs, item));
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

    @SubCommand("setItem {id}")
    public void setVoteRewardItem(CorePlayer player, String id) {
        CoreItem item = player.interact().getItemInHand();
        if(item == null){
            player.warn("no_item_in_hand").handle();
            return;
        }
        try{
            VoteReward reward = this.service.findVoteReward(id);
            reward.setDisplayItem(preparedItem(id, reward.getCosts(), item));
            this.service.update(reward);
            player.info("Vote Reward display item changed").handle();
        }catch (RuntimeException re){
            player.warn("Vote Reward with id: " + id + " not exists").handle();
        }
    }

    private CoreItem[] preparedItem(String rewardId, int costs, CoreItem hand){
        CoreItem clone = server.items().createNoModifier(hand.clone());
        clone.bindCommand("vote collect reward " + rewardId)
                .setName("??7Reward: ??e" + rewardId.replaceAll("_", " "))
                .addToLore("   ??7Costs: ??c" + costs);
        return new CoreItem[]{clone};
    }

    @SubCommand("delete {id}")
    public void deleteVoteReward(CorePlayer player, String id) {
        try{
            VoteReward reward = this.service.findVoteReward(id);
            this.service.deleteReward(reward);
            inventoryService.remove("inventory_vote_reward_" + id);
            itemCollectionService.remove(itemCollectionService.findOrCreate("inventory_vote_reward_" + id));
            player.info("Vote Reward with id: " + id + " has been deleted").handle();
        }catch (RuntimeException re){
            player.warn("Vote Reward with id: " + id + " not exists").handle();
        }
    }

    @SubCommand("edit {id}")
    public void editRewardsForVoteReward(CorePlayer player, String id) {
        try{
            this.service.findVoteReward(id);
            WrappedInventory inv = this.inventoryService.findOrCreate("inventory_vote_reward_" + id, "??aReward: " + id, (byte) 18);
            player.inventories().openLowerEnabledInventory(inv);
        }catch (RuntimeException re){
            player.warn("Vote Reward with id: " + id + " not exists").handle();
        }
    }
}
