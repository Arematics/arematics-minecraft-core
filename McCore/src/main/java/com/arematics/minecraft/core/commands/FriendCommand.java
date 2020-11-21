package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.annotations.Validator;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.validator.FriendValidator;
import com.arematics.minecraft.core.command.processor.validator.RequestValidator;
import com.arematics.minecraft.core.command.supplier.page.PageCommandSupplier;
import com.arematics.minecraft.core.items.Items;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.*;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.pages.Page;
import com.arematics.minecraft.core.pages.Pageable;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class FriendCommand extends CoreCommand {
    public static final String PAGER_KEY = "friends";

    private final Map<User, User> friendInvites = new HashMap<>();

    private UserService service;

    @Autowired
    public FriendCommand(UserService userService){
        super("friend", "friends", "freunde");
        this.service = userService;
    }

    @Override
    public void onDefaultExecute(CommandSender sender) {
        if(!(sender instanceof Player)){
            Messages.create("Only Players allowed to perform this command")
                    .WARNING()
                    .to(sender)
                    .handle();
            return;
        }
        CorePlayer player = CorePlayer.get((Player)sender);
        Pageable pageable = player.getPager().fetchOrCreate(FriendCommand.PAGER_KEY, this::getFriends);
        PageCommandSupplier.create(pageable.current()).setCLI(this::onCLI).setUI(this::onUI).accept(player);
    }

    @SubCommand("add {name}")
    public void addFriend(CorePlayer player,
                          @Validator(validators = {FriendValidator.class, RequestValidator.class})
                                  User target) {
        User user = service.getOrCreateUser(player);
        CorePlayer invited = CorePlayer.get(Bukkit.getPlayer(target.getUuid()));
        invited.info("Player %inviter% would be your friend. %accept% | %deny%")
                .setInjector(AdvancedMessageInjector.class)
                .replace("inviter", new Part(player.getPlayer().getName()))
                .replace("accept", PartBuilder.createHoverAndRun("ACCEPT", "§aAccept friend request",
                        "/friend accept " + player.getPlayer().getName()).setBaseColor(JsonColor.GREEN))
                .replace("deny", PartBuilder.createHoverAndRun("DENY", "§cDeny friend request",
                        "/friend deny " + player.getPlayer().getName()).setBaseColor(JsonColor.RED))
                .handle();
        player.info("Friend request send to " + invited.getPlayer().getName()).handle();
        invited.getRequestSettings().addTimeout(player.getPlayer().getName());
        friendInvites.put(user, target);
        ArematicsExecutor.asyncDelayed(() -> friendInvites.remove(user, target), 2, TimeUnit.MINUTES);
    }

    @SubCommand("remove {name}")
    public void removeFriend(CorePlayer player, User target) {
        User user = service.getOrCreateUser(player);
        if(!user.getFriends().contains(target)){
            player.warn("Player " + target.getLastName() + " is not your friend").handle();
        }else{
            user.getFriends().remove(target);
            service.update(user);
            target.getFriends().remove(user);
            service.update(target);
            player.info("Successful removed friend " + target.getLastName()).handle();
            player.getPager()
                    .fetchOrCreate(FriendCommand.PAGER_KEY, this::getFriends)
                    .remove(target.getLastName());
            if(Bukkit.getPlayer(target.getUuid()) != null)
                CorePlayer.get(Bukkit.getPlayer(target.getUuid())).getPager()
                        .fetchOrCreate(FriendCommand.PAGER_KEY, this::getFriends)
                        .remove(user.getLastName());
        }
    }

    @SubCommand("accept {name}")
    public boolean acceptFriendRequest(CorePlayer player, User requester) {
        User user = service.getOrCreateUser(player);
        if(friendInvites.containsKey(requester)){
            User target = friendInvites.get(requester);
            if(!target.getUuid().equals(user.getUuid())){
                Messages.create("You got no invite from this player").WARNING().to(player.getPlayer()).handle();
                return true;
            }else{
                requester.getFriends().add(user);
                service.update(requester);
                user.getFriends().add(requester);
                service.update(user);
                Messages.create("Friend request accepted").to(player.getPlayer()).handle();
                Messages.create(player.getPlayer().getName() + " accepted your friend request")
                        .to(Bukkit.getPlayer(requester.getUuid()))
                        .handle();
                player.getPager()
                        .fetchOrCreate(FriendCommand.PAGER_KEY, this::getFriends)
                        .add(requester.getLastName());
                CorePlayer.get(Bukkit.getPlayer(requester.getUuid())).getPager()
                        .fetchOrCreate(FriendCommand.PAGER_KEY, this::getFriends)
                        .add(user.getLastName());
                friendInvites.remove(requester, target);
            }
        }
        return true;
    }

    private List<String> getFriends(CorePlayer player){
        return service.getOrCreateUser(player.getUUID(), player.getPlayer().getName()).getFriends()
                .stream()
                .map(User::getLastName)
                .collect(Collectors.toList());
    }

    private boolean onCLI(CorePlayer player, Page page){
        String msg = "§a\n\n§7Friends" + " » " + "%friends%";
        MSG result = page == null ? new MSG("-") : MSGBuilder.join(page.getContent().stream()
                .map(friend -> PartBuilder.createHoverAndSuggest(friend, "Remove friend " + friend,
                        "/friend remove " + friend))
                .collect(Collectors.toList()), ',');
        result.PARTS.forEach(part -> part.setBaseColor(JsonColor.RED));
        player.info(msg)
                .setInjector(AdvancedMessageInjector.class)
                .disableServerPrefix()
                .replace("friends", result)
                .handle();
        return true;
    }

    private boolean onUI(CorePlayer player, Page page){
        if(page == null) page = new Page(new ArrayList<>());
        Inventory inventory = page.getInventory();
        if(inventory == null) {
            inventory = Bukkit.createInventory(null, 54, "§cFriends");
            for (int i = 0; i <= 8; i++) inventory.setItem(i, Items.PLAYERHOLDER);
            for (int i = 45; i <= 53; i++) inventory.setItem(i, Items.PLAYERHOLDER);
            if (player.getPager().fetch(FriendCommand.PAGER_KEY).hasBefore()) inventory.setItem(45, Items.BEFORE_PAGE);
            if (player.getPager().fetch(FriendCommand.PAGER_KEY).hasNext()) inventory.setItem(53, Items.NEXT_PAGE);
            int slot = 9;
            for (String name : page.getContent()){
                User user = this.service.findByName(name);
                inventory.setItem(slot++, Items.fetchPlayerSkull(user.getLastName())
                        .bindCommand("friend remove " + user.getLastName())
                        .setName("§8Player: §c" + user.getLastName())
                        .addToLore("§cClick to remove friend"));
            }
            page.setInventory(inventory);
        }
        Inventory finalInventory = inventory;
        player.openInventory(finalInventory);
        return true;
    }
}
