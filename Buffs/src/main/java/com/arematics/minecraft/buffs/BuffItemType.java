package com.arematics.minecraft.buffs;

import com.arematics.minecraft.buffs.server.PlayerBuffHandler;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.command.processor.parser.PeriodParser;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.items.parser.ItemType;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.PlayerBuff;
import com.arematics.minecraft.data.service.PlayerBuffService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.potion.PotionEffectType;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class BuffItemType extends ItemType {

    private final Random random = new Random();
    private final PlayerBuffService service;
    private final Server server;
    private final PeriodParser periodParser;

    @Override
    public String propertyValue() {
        return "Buff";
    }

    @Override
    public Part execute(CorePlayer player, CoreItem item) {
        try{
            String buffType = item.getMeta().getString(propertyValue());
            PotionEffectType effectType = buffType.equalsIgnoreCase("random") ?
                    getRandom() :
                    PotionEffectType.getByName(buffType);
            if(effectType == null) throw new CommandProcessException("Could not fetch any potion effect type");
            String strengthRaw = item.getMeta().getString("Buff-Strength");
            byte strength = 0;
            try{
                strength = Byte.parseByte(strengthRaw);
            }catch (Exception ignore){}
            String buffTime = item.getMeta().getString("Buff-Time");
            Period time = null;
            if(StringUtils.isNotBlank(buffTime)) time = periodParser.parse(buffTime);
            PlayerBuff buff;
            boolean newBuff = false;
            try{
                buff = service.findValidBuff(player.getUUID(), effectType);
            }catch (RuntimeException re){
                buff = new PlayerBuff();
                buff.setId(player.getUUID());
                buff.setPotionEffectType(effectType.getName());
                newBuff = true;
            }
            buff.setStrength(strength);
            buff.setActive(buff.isActive());
            if(time != null) {
                LocalDateTime result = LocalDateTime.now().plusSeconds(time.toStandardSeconds().getSeconds());
                if (newBuff || (buff.getEndTime() != null && buff.getEndTime().toLocalDateTime().isBefore(result)))
                    buff.setEndTime(Timestamp.valueOf(result));
            }
            service.update(buff);
            return new Part(effectType.getName() + " Buff")
                    .setHoverActionShowItem(buff.mapToItem(server));
        }catch (Exception ignore){ }
        throw new RuntimeException("Something went wrong");
    }

    private PotionEffectType getRandom(){
        return PlayerBuffHandler.POSITIVE_BUFFS[random.nextInt(PlayerBuffHandler.POSITIVE_BUFFS.length)];
    }
}
