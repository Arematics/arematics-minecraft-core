package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.annotations.Validator;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.validator.NotSelfValidator;
import com.arematics.minecraft.core.messaging.advanced.MSGBuilder;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.Rank;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.redis.GlobalRedisMessagePublisher;
import com.arematics.minecraft.data.service.RankPermissionService;
import com.arematics.minecraft.data.service.RankService;
import com.arematics.minecraft.data.service.UserService;
import com.arematics.minecraft.data.share.model.RanksPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Perm(permission = "*", description = "GRANT ALL access")
public class RankCommand extends CoreCommand {

    private final GlobalRedisMessagePublisher globalRedisMessagePublisher;
    private final UserService userService;
    private final RankService rankService;
    private final RankPermissionService rankPermissionService;

    @Autowired
    public RankCommand(GlobalRedisMessagePublisher globalRedisMessagePublisher,
                       UserService userService,
                       RankService rankService,
                       RankPermissionService rankPermissionService){
        super("rank", true);
        this.globalRedisMessagePublisher = globalRedisMessagePublisher;
        this.userService = userService;
        this.rankService = rankService;
        this.rankPermissionService = rankPermissionService;
    }

    @SubCommand("set {user} {rank}")
    public void setUserRanks(@Validator(validators = NotSelfValidator.class) CorePlayer sender,
                             User target,
                             Rank rank) {
        target.setRank(rank);
        userService.update(target);
        globalRedisMessagePublisher.publish(userService.messageKey(), target.getUuid().toString());
        sender.info("Rank changed for player: " + target.getLastName());
    }

    @SubCommand("setdisplay {user} {rank}")
    public void setUserDisplayRank(@Validator(validators = NotSelfValidator.class) CorePlayer sender,
                             User target,
                             Rank rank) {
        target.setDisplayRank(rank);
        userService.update(target);
        globalRedisMessagePublisher.publish(userService.messageKey(), target.getUuid().toString());
        sender.info("Display Rank changed for player: " + target.getLastName());
    }

    @SubCommand("add perm {rank} {permission}")
    public void addPermissionToRank(CorePlayer sender, Rank rank, String permission) {
        try{
            RanksPermission data = new RanksPermission(rank.getId(), permission, null);
            rankPermissionService.updatePerm(data);
            sender.info("Permission added").handle();
        }catch (Exception e){
            sender.warn("Permission already exits on rank").handle();
        }
    }

    @SubCommand("rem perm {rank} {permission}")
    public void remPermissionFromRank(CorePlayer sender, Rank rank, String permission) {
        try{
            RanksPermission data = new RanksPermission(rank.getId(), permission, null);
            rankPermissionService.delete(data);
            sender.info("Permission deleted").handle();
        }catch (Exception e){
            sender.warn("Permission not exits on rank").handle();
        }
    }

    @SubCommand("add modeperm {rank} {permission}")
    public void addModePermissionToRank(CorePlayer sender, Rank rank, String permission) {
        try{
            RanksPermission data = new RanksPermission(rank.getId(), permission, null);
            rankPermissionService.updateModePerm(data);
            sender.info("Permission added").handle();
        }catch (Exception e){
            sender.warn("Permission already exits on rank").handle();
        }
    }

    @SubCommand("rem modeperm {rank} {permission}")
    public void remModePermissionFromRank(CorePlayer sender, Rank rank, String permission) {
        try{
            RanksPermission data = new RanksPermission(rank.getId(), permission, null);
            rankPermissionService.deleteMode(data);
            sender.info("Permission deleted").handle();
        }catch (Exception e){
            sender.warn("Permission not exits on rank").handle();
        }
    }

    @SubCommand("list perms {rank}")
    public void listGlobalPerms(CorePlayer sender, Rank rank) {
        List<Part> permissions = rankPermissionService.listAllPermsForRank(rank).stream()
                .map(perm -> toPart(perm, rank, false))
                .collect(Collectors.toList());
        sender.info("listing")
                .setInjector(AdvancedMessageInjector.class)
                .replace("list_type", new Part("Permission"))
                .replace("list_value", MSGBuilder.join(permissions, ','))
                .handle();

    }

    @SubCommand("list modeperms {rank}")
    public void listModePerms(CorePlayer sender, Rank rank) {
        List<Part> permissions = rankPermissionService.listAllModePermsForRank(rank).stream()
                .map(perm -> toPart(perm, rank, true))
                .collect(Collectors.toList());
        sender.info("listing")
                .setInjector(AdvancedMessageInjector.class)
                .replace("list_type", new Part("Mode Permission"))
                .replace("list_value", MSGBuilder.join(permissions, ','))
                .handle();
    }

    private Part toPart(String permission, Rank rank, boolean mode){
        return PartBuilder.createHoverAndSuggest(permission, "Remove permission",
                "/rank rem " + (mode ? "modeperm" : "perm") + " " + rank.getName() + " " + permission);
    }
}
