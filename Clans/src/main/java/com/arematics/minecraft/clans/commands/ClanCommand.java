package com.arematics.minecraft.clans.commands;

import com.arematics.minecraft.clans.commands.validator.InClanValidator;
import com.arematics.minecraft.clans.commands.validator.NoClanValidator;
import com.arematics.minecraft.clans.utils.ClanPermissionArray;
import com.arematics.minecraft.clans.utils.ClanPermissions;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.annotations.Validator;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.messaging.Messages;
import com.arematics.minecraft.core.messaging.advanced.JsonColor;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.mode.model.*;
import com.arematics.minecraft.data.service.ClanMemberService;
import com.arematics.minecraft.data.service.ClanRankService;
import com.arematics.minecraft.data.service.ClanService;
import com.arematics.minecraft.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class ClanCommand extends CoreCommand {

    private final Map<ClanInvite, CorePlayer> clanInvites = new HashMap<>();

    private final ClanService clanService;
    private final ClanMemberService clanMemberService;
    private final ClanRankService clanRankService;
    private final UserService userService;

    @Autowired
    public ClanCommand(ClanService clanService,
                       ClanMemberService clanMemberService,
                       ClanRankService clanRankService,
                       UserService userService){
        super("clan", "clans", "c");
        this.clanService = clanService;
        this.clanMemberService = clanMemberService;
        this.clanRankService = clanRankService;
        this.userService = userService;
    }

    @SubCommand("create {name} {tag}")
    public boolean createClan(@Validator(validators = NoClanValidator.class) CorePlayer player, String name, String tag)
            throws CommandProcessException {
        final String clanExists = "Clan with %typ% %value% already exists";
        try{
            clanService.findClanByName(name);
            player.warn(clanExists)
                    .DEFAULT()
                    .replace("typ", "name")
                    .replace("value", name)
                    .handle();
        }catch (RuntimeException re){
            try{
                clanService.findClanByTag(tag);
                player.warn(clanExists)
                        .DEFAULT()
                        .replace("typ", "tag")
                        .replace("value", tag)
                        .handle();
            }catch (RuntimeException re2){
                createNewClan(player, name, tag);
            }
        }
        return true;
    }
    
    @SubCommand("delete")
    public void deleteClan(@Validator(validators = InClanValidator.class) CorePlayer player)
            throws CommandProcessException {
        ClanMember member = clanMemberService.getMember(player);
        if(!ClanPermissions.isAdmin(member)){
            player.warn("Not permitted to perform this command for your clan").handle();
            return;
        }
        Clan clan = clanService.findClanById(member.getRank().getClanRankId().getClanId());
        clan.getAllOnline().forEach(clanPlayer -> Messages.create("Your clan has been deleted by an admin")
                .WARNING().to(clanPlayer).handle());
        clanService.delete(clan);
    }

    @SubCommand("invite {name}")
    public void invitePlayer(@Validator(validators = InClanValidator.class)
                                         CorePlayer player,
                             @Validator(validators = NoClanValidator.class)
                                     CorePlayer target) throws CommandProcessException {
        ClanMember member = clanMemberService.getMember(player);
        if(!ClanPermissions.canInvite(member))
            throw new CommandProcessException("Not permitted to perform this command for your clan");
        target.getRequestSettings().checkAllowed(userService.getOrCreateUser(player));
        Clan clan = clanService.findClanById(member.getRank().getClanRankId().getClanId());
        if(clan.getSlots() <= clan.getMembers().size()) throw new CommandProcessException("Your clan is full");
        String clanName = clan.getName();
        target.info("You have been invited to join clan %clan%. %accept% | %deny%")
                .setInjector(AdvancedMessageInjector.class)
                .replace("clan", new MSG(clanName))
                .replace("accept", PartBuilder.createHoverAndRun("ACCEPT", "§aAccept clan request",
                        "/clan accept " + clanName).setBaseColor(JsonColor.GREEN))
                .replace("deny", PartBuilder.createHoverAndRun("DENY", "§cDeny clan request",
                        "/clan deny " + clanName).setBaseColor(JsonColor.RED))
                .handle();
        player.info("Clan request send to " + target.getPlayer().getName()).handle();
        target.getRequestSettings().addTimeout(player.getPlayer().getName());
        ClanInvite inviteKey = new ClanInvite(player, clan);
        clanInvites.put(inviteKey, target);
        ArematicsExecutor.asyncDelayed(() -> clanInvites.remove(inviteKey, target), 2, TimeUnit.MINUTES);

    }

    @SubCommand("accept {clan}")
    public void acceptClanRequest(@Validator(validators = NoClanValidator.class) CorePlayer player, Clan clan) {
        if(clanInvites.containsValue(player)){
            ClanInvite invation = clanInvites.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(player))
                    .findFirst()
                    .filter(entry -> entry.getKey().getClan().getName().equals(clan.getName()))
                    .map(Map.Entry::getKey)
                    .orElse(null);
            if(invation == null){
                player.warn("You got no invite for clan " + clan.getName()).handle();
            }else{
                ClanRank rank = clanRankService.getClanRank(new ClanRankId(clan.getId(), "Member"));
                ClanMember member = new ClanMember(player.getUUID(), rank, 0, 0);
                clanMemberService.update(member);
                clan.getMembers().add(member);
                clanService.update(clan);
                clan.getAllOnline().forEach(online -> Messages
                        .create("Player " + player.getPlayer().getName() + " joined the clan")
                        .to(online)
                        .handle());
                clanInvites.remove(invation, player);
            }
        }
    }

    @SubCommand("deny {clan}")
    public void denyClanRequest(CorePlayer player, Clan clan) {
        clanInvites.entrySet().stream()
                .filter(entry -> entry.getValue().equals(player))
                .findFirst()
                .filter(entry -> entry.getKey().getClan().equals(clan))
                .map(Map.Entry::getKey).ifPresent(invation -> denyInvation(invation, player));
    }

    private void denyInvation(ClanInvite invite, CorePlayer player){
        clanInvites.remove(invite, player);
        player.info("Clan invite denied").handle();
    }

    @SubCommand("remove {user}")
    public void removeClanMember(@Validator(validators = InClanValidator.class) CorePlayer player, User target) {
        ClanMember member = clanMemberService.getMember(player);
        if(!ClanPermissions.canKick(member)){
            player.warn("Not permitted to perform this command for your clan").handle();
            return;
        }
        try{
            ClanMember targetMember = clanMemberService.getMember(target.getUuid());
            Clan clan = clanService.findClanById(member.getRank().getClanRankId().getClanId());
            if(!targetMember.getRank().getClanRankId().getClanId().equals(clan.getId()))
                throw new CommandProcessException("Not same clan");
            if(!ClanPermissions.rankLevelCorrect(member, targetMember))
                throw new CommandProcessException("Not allowed to kick this player");
            clan.getAllOnline().forEach(clanPlayer ->
                    Messages.create("Player " + target.getLastName() + " got kicked").to(clanPlayer).handle());
            clan.getMembers().remove(targetMember);
            clanService.update(clan);
            clanMemberService.delete(targetMember);
        }catch (RuntimeException re){
            throw new CommandProcessException("Player " + target.getLastName() + " is not in a clan");
        }
    }

    @SubCommand("leave")
    public void leaveClan(@Validator(validators = InClanValidator.class) CorePlayer player) {
        ClanMember member = clanMemberService.getMember(player);
        if(ClanPermissions.isAdmin(member))
            throw new CommandProcessException("As admin you can not leave, you need to use /clan delete.");
        else{
            Clan clan = clanService.findClanById(member.getRank().getClanRankId().getClanId());
            clan.getAllOnline().forEach(clanPlayer ->
                    Messages.create("Player " + player.getPlayer().getName() + " has left the clan").to(clanPlayer).handle());
            clan.getMembers().remove(member);
            clanService.update(clan);
            clanMemberService.delete(member);
            player.info("You have left the clan").handle();
        }
    }

    @SubCommand("rang {user}")
    public void setClanRang(@Validator(validators = InClanValidator.class) CorePlayer player, User target) {
    }

    @SubCommand("stats {clan}")
    public void getClanStats(CorePlayer player, Clan clan) {
    }

    @SubCommand("money add {amount}")
    public void addClanMoney(@Validator(validators = InClanValidator.class) CorePlayer player, Double amount) {
        player.warn("Not implemented yet").handle();
    }

    @SubCommand("money rem {amount}")
    public void removeClanMoney(@Validator(validators = InClanValidator.class) CorePlayer player, Double amount) {
        player.warn("Not implemented yet").handle();
    }

    @SubCommand("shop")
    public void clanShop(@Validator(validators = InClanValidator.class) CorePlayer player) {
        player.warn("Not implemented yet").handle();
    }

    private void createNewClan(CorePlayer player, String name, String tag){
        Clan clan = clanService.createClan(name, tag);
        List<ClanRank> ranks = generateDefaultClanRanks(clan);
        clan.setRanks(new HashSet<>(ranks));
        ClanMember member = new ClanMember(player.getUUID(), ranks.get(0), 0, 0);
        clanMemberService.update(member);
        clan.getMembers().add(member);
        clanService.update(clan);
        player.info("Clan created successfully").handle();
    }

    private List<ClanRank> generateDefaultClanRanks(Clan clan){
        ClanRank owner = new ClanRank(ClanRankId.of(clan.getId(), "Owner"),
                "§4", 0, ClanPermissionArray.of("admin"));
        ClanRank moderator = new ClanRank(ClanRankId.of(clan.getId(), "Moderator"),
                "§4", 1, ClanPermissionArray.of("invite", "remove", "use-money"));
        ClanRank member = new ClanRank(ClanRankId.of(clan.getId(), "Member"),
                "§4", 2, ClanPermissionArray.of("invite"));
        return new ArrayList<ClanRank>(){{
            add(clanRankService.save(owner));
            add(clanRankService.save(moderator));
            add(clanRankService.save(member));
        }};
    }
}
