package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.annotations.Validator;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.command.processor.validator.NotSelfValidator;
import com.arematics.minecraft.core.command.processor.validator.PositiveNumberValidator;
import com.arematics.minecraft.core.command.processor.validator.RequestValidator;
import com.arematics.minecraft.core.events.CurrencyEventType;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.CommandUtils;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.mode.model.GameStats;
import com.arematics.minecraft.data.service.GameStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Perm(permission = "management.player.money", description = "Money Command")
public class MoneyCommand extends CoreCommand {

    private final Server server;
    private final GameStatsService gameStatsService;
    private final Map<CorePlayer, Long> cooldown = new HashMap<>();

    @Autowired
    public MoneyCommand(Server server, GameStatsService gameStatsService){
        super("money");
        this.server = server;
        this.gameStatsService = gameStatsService;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        showMoney(sender, sender.getUser());
    }

    @SubCommand("{target}")
    public void showOtherMoney(CorePlayer sender, User target) {
        showMoney(sender, target);
    }

    @SubCommand("pay {target} {amount}")
    public void sendMoneyToPlayer(CorePlayer sender,
                                  @Validator(validators = {NotSelfValidator.class, RequestValidator.class})CorePlayer target,
                                  @Validator(validators = PositiveNumberValidator.class) Double amount) {
        if(cooldown.containsKey(sender) && cooldown.get(sender) > System.currentTimeMillis())
            throw new CommandProcessException("Please wait some time until making more payments");
        if(sender.getMoney() < amount)
            throw new CommandProcessException("You dont have enough money to afford this");
        boolean success = this.server.currencyController()
                .createEvent(sender)
                .setAmount(amount)
                .setEventType(CurrencyEventType.TRANSFER)
                .setTarget(target.getUUID().toString())
                .removeMoney();
        if(success){
            target.addMoney(amount);
            sender.info("You have payed " + CommandUtils.prettyDecimal(amount) + " coins to " + target.getPlayer().getName()).handle();
            target.info("Player " + sender.getPlayer().getName() + " send you " + CommandUtils.prettyDecimal(amount) + " coins").handle();
            cooldown.put(sender, System.currentTimeMillis() + (1000 * 10));
        }
    }

    @SubCommand("add {target} {amount}")
    @Perm(permission = "add", description = "Add Money to player")
    public void addMoneyToPlayer(CorePlayer sender,
                                 CorePlayer target,
                                 @Validator(validators = PositiveNumberValidator.class) Double amount) {
        boolean success = this.server.currencyController()
                .createEvent(sender)
                .setAmount(amount)
                .setEventType(CurrencyEventType.GENERATE)
                .setTarget(target.getUUID().toString())
                .onSuccess(() -> target.addMoney(amount));
        if(success){
            sender.info("You have add " + CommandUtils.prettyDecimal(amount) + " coins to " + target.getPlayer().getName()).handle();
            target.info("You got " + CommandUtils.prettyDecimal(amount) + " coins").handle();
        }
        target.requests().addTimeout(sender.getName());
    }

    @SubCommand("remove {target} {amount}")
    @Perm(permission = "remove", description = "Remove Money from player")
    public void removeMoneyToPlayer(CorePlayer sender,
                                 CorePlayer target,
                                 @Validator(validators = PositiveNumberValidator.class) Double amount) {
        if(target.getMoney() < amount)
            throw new CommandProcessException("Player dont have enough money for this");
        boolean success = this.server.currencyController()
                .createEvent(sender)
                .setAmount(amount)
                .setEventType(CurrencyEventType.WASTE)
                .setTarget(target.getUUID().toString())
                .onSuccess(() -> target.removeMoney(amount));
        if(success){
            sender.info("You have removed " + CommandUtils.prettyDecimal(amount) + " coins from " + target.getPlayer().getName()).handle();
        }
    }

    private void showMoney(CorePlayer sender, User target){
        GameStats stats = gameStatsService.getOrCreate(target.getUuid());
        sender.info(target.getLastName() + "'s Money: %money%")
                .DEFAULT()
                .replace("money", CommandUtils.prettyDecimal(stats.getCoins()) + " Coins")
                .handle();
    }
}
