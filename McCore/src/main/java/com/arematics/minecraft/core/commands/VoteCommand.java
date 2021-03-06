package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.utils.CommandUtils;
import com.arematics.minecraft.data.global.model.PlayerVotes;
import com.arematics.minecraft.data.mode.model.VoteReward;
import com.arematics.minecraft.data.service.ItemCollectionService;
import com.arematics.minecraft.data.service.PlayerVotesService;
import com.arematics.minecraft.data.service.VoteRewardService;
import org.bukkit.Material;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@Perm(permission = "server.vote", description = "Vote Command Permission")
public class VoteCommand extends CoreCommand {

    private final PlayerVotesService votesService;
    private final VoteRewardService voteRewardService;
    private final ItemCollectionService itemCollectionService;

    @Autowired
    public VoteCommand(PlayerVotesService playerVotesService,
                       VoteRewardService voteRewardService,
                       ItemCollectionService itemCollectionService){
        super("vote", "votes");
        this.votesService = playerVotesService;
        this.voteRewardService = voteRewardService;
        this.itemCollectionService = itemCollectionService;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        String header = CommandUtils.prettyHeader("Votes", "Information");
        sender.info(header).DEFAULT().disableServerPrefix().handle();
        Part part = PartBuilder.createHoverAndLink("    ??7> Vote Seite 1",
                "??8Click to open vote page",
                "http://mcsl.name/55690/" + sender.getName());
        sender.send(part).disableServerPrefix().handle();
    }

    @SubCommand("data")
    public void listOwnVoteData(CorePlayer sender) {
        List<VoteReward> rewards = this.voteRewardService.findAll();
        Supplier<PlayerVotes> votes = () -> this.votesService.getOrCreate(sender.getUUID());
        CoreItem info = generatePlayerInfo(votes.get());
        InventoryBuilder builder = InventoryBuilder.create("Votes", 4, sender)
                .openBlocked(sender)
                .addItem(info, 1, 5);
        sender.inventories().registerItemClick(info, (item) -> generatePlayerInfo(votes.get()));
        int row = 3;
        int slot = 2;
        for(VoteReward reward : rewards){
            CoreItem item = reward.getDisplayItem()[0];
            builder.addItem(item, row, slot);
            slot += 2;
            if(slot > 9){
                row++;
                slot = 2;
            }
        }
    }

    private CoreItem generatePlayerInfo(PlayerVotes playerVotes){
        return server.items().generateNoModifier(Material.BOOK)
                .disableClick()
                .addToLore("    ??8> Your Vote Points: ??e" + playerVotes.getCurrentVotePoints())
                .addToLore("    ??8> Your Streak: ??e" + playerVotes.getStreak() + " Days")
                .addToLore("    ??8> Total Votes: ??e" + playerVotes.getTotalVotes())
                .addToLore("    ??8> Free Streak Skips: ??e" + playerVotes.getFreeVoteSkips() + " Days")
                .setName("??7Your Vote Data");
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
        CoreItem[] contents;
        try{
            contents = this.itemCollectionService.findItemCollection("inventory_vote_reward_" + rewardId).getItems();
        }catch (RuntimeException re){
            player.failure("No reward items found. Please report this").handle();
            return;
        }
        List<CoreItem> itemList = Arrays.stream(contents)
                .filter(Objects::nonNull)
                .map(server.items()::create)
                .collect(Collectors.toList());
        server.items().giveItemsTo(player, itemList);
        votes.setCurrentVotePoints(votes.getCurrentVotePoints() - reward.getCosts());
        this.votesService.updatePlayerVotes(votes);
        player.info("You have received your vote reward").handle();
    }
}
