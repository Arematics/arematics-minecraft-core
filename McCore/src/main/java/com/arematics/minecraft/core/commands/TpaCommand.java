package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.annotations.Validator;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.validator.CombatValidator;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.server.controller.PlayerTeleportController;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
@Perm(permission = "tpa", description = "Teleport to a player")
public class TpaCommand extends CoreCommand {

    private final PlayerTeleportController teleportController;

    @Autowired
    public TpaCommand(PlayerTeleportController teleportController) {
        super("tpa");
        this.teleportController = teleportController;
    }

    @SubCommand("{player}")
    public void sendTpa(@Validator(validators = CombatValidator.class) CorePlayer player, CorePlayer target) {
       getTeleportController().sendTpaRequest(player, target);
        System.out.println("b");
    }

    @SubCommand("accept")
    public void acceptTpa(@Validator(validators = CombatValidator.class) CorePlayer receiver) {
        // unsere tpa request, null wenn keine vorhanden
        CorePlayer tpaSender = getTeleportController().getRequest(receiver);
        // wenn request vorhanden
        System.out.println("a");
        if(null != tpaSender) {
            if(getTeleportController().accept(tpaSender, receiver)) {
                tpaSender.getPlayer().sendMessage("Du wurdest zu " + receiver.getPlayer().getDisplayName() + " teleportiert");
                receiver.getPlayer().sendMessage(receiver.getPlayer().getDisplayName() + " wurde zu dir teleportiert");
            }else {
                tpaSender.getPlayer().sendMessage("tpa konnte nicht durchgeführt werden");
            }
        } else {
            receiver.getPlayer().sendMessage("keine request zum annehmen!");
        }
    }

    @SubCommand("deny")
    public void denyTpa(@Validator(validators = CombatValidator.class) CorePlayer receiver) {
        CorePlayer tpaSender = getTeleportController().getRequest(receiver);
        if(null != tpaSender) {
            getTeleportController().deny(receiver);
            tpaSender.getPlayer().sendMessage(receiver.getPlayer().getDisplayName() + " hat deine Anfrage abgelehnt");
            receiver.getPlayer().sendMessage("Du hast " + receiver.getPlayer().getDisplayName() + " Anfrage abgelehnt");
        } else {
            receiver.getPlayer().sendMessage("keine request zum ablehnen");
        }
    }

}
