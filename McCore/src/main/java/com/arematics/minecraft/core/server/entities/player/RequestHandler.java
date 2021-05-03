package com.arematics.minecraft.core.server.entities.player;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.global.model.Configuration;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.FriendService;
import com.arematics.minecraft.data.service.IgnoredService;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RequestHandler extends PlayerHandler {

    private final Set<String> timeouts = new HashSet<>();

    public RequestFilter getRequestFilter(){
        return RequestFilter.valueOf(player.getUser().getConfigurations()
                .getOrDefault("requestFilter", new Configuration(RequestFilter.EVERYONE.toString())).getValue());
    }

    public int getRequestTimeout(){
        return Integer.parseInt(player.getUser().getConfigurations()
                .getOrDefault("requestTimeout", new Configuration("5")).getValue());
    }

    public void setRequestFilter(RequestFilter filter){
        User user = player.getUser();
        user.getConfigurations().put("requestFilter", new Configuration(filter.toString()));
        player.update(user);
    }

    public void setRequestTimeout(int seconds){
        User user = player.getUser();
        user.getConfigurations().put("requestTimeout", new Configuration("" + seconds));
        player.update(user);
    }

    public void checkAllowed(CorePlayer requester) throws CommandProcessException {
        IgnoredService service = Boots.getBoot(CoreBoot.class).getContext().getBean(IgnoredService.class);
        if(service.hasIgnored(player.getUUID(), requester.getUUID()))
            throw new CommandProcessException("This player does not accept requests");
        if(hasTimeout(requester.getName()))
            throw new CommandProcessException("You must wait to send this player an request again");
        FriendService friendService = Boots.getBoot(CoreBoot.class).getContext().getBean(FriendService.class);
        switch(getRequestFilter()){
            case FRIENDS:
                if(!friendService.isFriended(player.getUUID(), requester.getUUID()))
                    throw new CommandProcessException("This player is accepting requests from friends only");
                else
                    break;
            case NOBODY:
                throw new CommandProcessException("This player does not accept requests");
        }
    }

    public boolean hasTimeout(String key){
        return timeouts.contains(key);
    }

    public void addTimeout(String key){
        ArematicsExecutor arematicsExecutor = Boots.getBoot(CoreBoot.class).getContext().getBean(ArematicsExecutor.class);
        this.timeouts.add(key);
        arematicsExecutor.asyncDelayed(() -> this.timeouts.remove(key), this.getRequestTimeout(), TimeUnit.SECONDS);
    }

    public void clearTimeouts(){
        this.timeouts.clear();
    }
}
