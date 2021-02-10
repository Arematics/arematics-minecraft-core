package com.arematics.minecraft.buffs.commands;

import com.arematics.minecraft.buffs.server.PlayerBuffHandler;
import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.injector.advanced.AdvancedMessageInjector;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.core.utils.CommandUtils;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.mode.model.PlayerBuff;
import org.bukkit.potion.PotionEffectType;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Perm(permission = "player.buffs.admin", description = "Permission to manage player buffs")
public class BuffAdminCommand extends CoreCommand {

    private final PlayerBuffHandler handler;

    @Autowired
    public BuffAdminCommand(PlayerBuffHandler playerBuffHandler){
        super("playerbuffs", "badmin");
        this.handler = playerBuffHandler;
    }

    @SubCommand("view {user}")
    public void viewUserBuffs(CorePlayer sender, User target) {
        List<PlayerBuff> buffs = handler.getPlayerBuffService().findActiveBuffsByPlayer(target.getUuid());
        sender.info(CommandUtils.prettyHeader("Buffs", target.getLastName())).DEFAULT().disableServerPrefix().handle();
        buffs.forEach(buff -> sender.info("%content%")
                .setInjector(AdvancedMessageInjector.class)
                .disableServerPrefix()
                .replace("content", toPrettyMessage(target, buff))
                .handle());
    }

    @SubCommand("add {user} {type} {strength}")
    public void addPotionEffectType(CorePlayer sender, User target, PotionEffectType potionEffectType, Byte strength) {
        addPotionEffectType(sender, target, potionEffectType, strength, null);
    }

    @SubCommand("add {user} {type} {strength} {time}")
    public void addPotionEffectType(CorePlayer sender, User target, PotionEffectType potionEffectType, Byte strength, Period period) {
        PlayerBuff buff = new PlayerBuff();
        buff.setId(target.getUuid());
        buff.setPotionEffectType(potionEffectType.getName());
        buff.setStrength(strength);
        if(period != null) {
            LocalDateTime result = LocalDateTime.now().plusSeconds(period.toStandardSeconds().getSeconds());
            buff.setEndTime(Timestamp.valueOf(result));
        }
        handler.addNewBuff(target.getUuid(), buff);
        sender.info("Buff for player has been activated").handle();
    }

    @SubCommand("remove {user} {type}")
    public void removePlayerBuff(CorePlayer sender, User target, PotionEffectType potionEffectType) {
        if(!handler.getPlayerBuffService().hasBuff(target.getUuid(), potionEffectType))
            throw new CommandProcessException("The player doesn't have this buff");
        PlayerBuff buff = handler.getPlayerBuffService().findBuff(target.getUuid(), potionEffectType);
        handler.removeBuff(target.getUuid(), buff);
        sender.info("Removed buff from player").handle();
    }

    private MSG toPrettyMessage(User target, PlayerBuff buff){
        String endString = buff.getEndTime() == null ? "Never" : fetchEndTime(buff);
        Part start = new Part("   §8>Type: " + buff.getPotionEffectType() + " ")
                .setHoverAction(HoverAction.SHOW_TEXT, "§8End: " + endString);
        Part remove = new Part("§c[X]").setHoverAction(HoverAction.SHOW_TEXT, "§cRemove Buff")
                .setClickAction(ClickAction.SUGGEST_COMMAND, "/playerbuffs remove " + target.getLastName() + " " + buff.getPotionEffectType());
        return new MSG(start, remove);
    }

    private String fetchEndTime(PlayerBuff buff){
        Duration between = Duration.between(LocalDateTime.now(), buff.getEndTime().toLocalDateTime());
        return TimeUtils.toString(Period.seconds((int) between.getSeconds()).normalizedStandard());
    }
}
