package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.annotations.Validator;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.validator.FriendValidator;
import com.arematics.minecraft.core.command.processor.validator.RequestValidator;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.advanced.JsonColor;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.inventories.InventoryBuilder;
import com.arematics.minecraft.core.server.entities.player.inventories.PageBinder;
import com.arematics.minecraft.core.server.entities.player.inventories.helper.Range;
import com.arematics.minecraft.core.server.entities.player.inventories.paging.Paging;
import com.arematics.minecraft.core.server.items.Items;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.global.model.Friend;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.FriendService;
import org.bukkit.Bukkit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class FriendCommand extends CoreCommand {
    private final Map<CorePlayer, CorePlayer> friendInvites = new HashMap<>();

    private final FriendService friendService;

    @Autowired
    public FriendCommand(FriendService friendService){
        super("friend", "friends", "freunde");
        this.friendService = friendService;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        Supplier<Page<Friend>> paging =
                () -> friendService.fetchAllFriends(sender.getUUID(), sender.inventories().getPage());
        Paging.create(sender, paging)
                .onCLI((friend) -> this.mapFriendToText(sender, friend), "Friend", "friends")
                .onGUI(this::createInventory)
                .execute();
    }

    @SubCommand("add {name}")
    public void addFriend(CorePlayer player,
                          @Validator(validators = {FriendValidator.class, RequestValidator.class})
                                  CorePlayer invited) {
        invited.info("player_friends_request")
                .setInjector(AdvancedMessageInjector.class)
                .replace("inviter", new Part(player.getPlayer().getName()))
                .handle();
        invited.info("&8« &a%previous% &8&l| &a%next% &8»")
                .setInjector(AdvancedMessageInjector.class)
                .disableServerPrefix()
                .replace("previous", PartBuilder.createHoverAndRun("ACCEPT", "§aAccept friend request",
                        "/friend accept " + player.getPlayer().getName()).setBaseColor(JsonColor.GREEN))
                .replace("next", PartBuilder.createHoverAndRun("DENY", "§cDeny friend request",
                        "/friend deny " + player.getPlayer().getName()).setBaseColor(JsonColor.RED))
                .handle();
        player.info("player_friends_request_send")
                .DEFAULT()
                .replace("invited", invited.getPlayer().getName())
                .handle();
        invited.requests().addTimeout(player.getPlayer().getName());
        friendInvites.put(player, invited);
        ArematicsExecutor.asyncDelayed(() -> friendInvites.remove(player, invited), 2, TimeUnit.MINUTES);
    }

    @SubCommand("remove {name}")
    public void removeFriend(CorePlayer player, User target) {
        try{
            Friend friend = friendService.getFriend(player.getUUID(), target.getUuid());
            friendService.unFriend(friend);
            player.info("player_friends_removed")
                    .DEFAULT()
                    .replace("name", target.getLastName())
                    .handle();
        }catch (RuntimeException runtimeException){
            player.warn("player_friends_nofriends")
                    .DEFAULT()
                    .replace("name", target.getLastName())
                    .handle();
        }
    }

    @SubCommand("accept {name}")
    public boolean acceptFriendRequest(CorePlayer player, CorePlayer requester) {
        if(friendInvites.containsKey(requester)){
            CorePlayer target = friendInvites.get(requester);
            if(!target.equals(player)){
                player.warn("player_friends_request_non").handle();
                return true;
            }else{
                friendService.friend(requester.getUUID(), player.getUUID());
                player.info("player_friends_request_accept")
                        .DEFAULT()
                        .replace("name", requester.getName())
                        .handle();
                requester.info("player_friends_request_has_accept")
                        .DEFAULT()
                        .replace("name", player.getName())
                        .handle();
                friendInvites.remove(requester, target);
            }
        }
        return true;
    }

    private void createInventory(CorePlayer sender, Supplier<Page<Friend>> paging){
        Range range = Range.allHardInRows(1, 7, 1, 2, 3, 4);
        PageBinder<Friend> binder = PageBinder.of(paging, range, (friend) -> this.mapFriendToItem(sender, friend));
        InventoryBuilder.create("Friends", 6, sender)
                .openBlocked(sender)
                .fillOuterLine()
                .bindPaging(sender, binder, true);
    }

    private MSG mapFriendToText(CorePlayer sender, Friend friend){
        UUID other = getOther(sender, friend);
        String name = Bukkit.getOfflinePlayer(other).getName();
        return new MSG(PartBuilder.createHoverAndSuggest(name, "Remove friend " + name,
                "/friend remove " + name));
    }

    private CoreItem mapFriendToItem(CorePlayer sender, Friend friend){
        UUID other = getOther(sender, friend);
        String name = Bukkit.getOfflinePlayer(other).getName();
        return Items.fetchPlayerSkull(name)
                .bindCommand("friend remove " + other)
                .setName("§8Player: §c" + name)
                .addToLore("§cClick to remove friend");
    }

    private UUID getOther(CorePlayer sender, Friend friend){
        return sender.getUUID().equals(friend.getUuid()) ? friend.getTargetUuid() : friend.getUuid();
    }
}
