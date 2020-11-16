package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.core.server.RequestFilter;
import com.arematics.minecraft.data.service.UserService;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestSettingsCommand extends CoreCommand {

    private final UserService service;

    @Autowired
    public RequestSettingsCommand(UserService userService){
        super("requests");
        this.service = userService;
    }
    
    @SubCommand("timeout {seconds}")
    public boolean setRequestTimeout(CorePlayer player, Period period) {
        player.getRequestSettings()
                .setRequestTimeout(service, service.getOrCreateUser(player), period.toStandardSeconds().getSeconds());
        player.getRequestSettings().clearTimeouts();
        player.info("Request timeout set to %seconds% seconds. Timeouts were reset.")
                .DEFAULT()
                .replace("seconds", "" + period.toStandardSeconds().getSeconds())
                .handle();
        return true;
    }

    @SubCommand("filter {filter}")
    public boolean setRequestScope(CorePlayer player, RequestFilter filter) {
        player.getRequestSettings()
                .setRequestFilter(service, service.getOrCreateUser(player), filter);
        player.info("Request filter set to scope " + filter.toString())
                .handle();
        return true;
    }
}
