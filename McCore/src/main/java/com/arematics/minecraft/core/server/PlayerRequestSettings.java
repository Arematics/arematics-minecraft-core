package com.arematics.minecraft.core.server;

import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.arematics.minecraft.data.global.model.Configuration;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PlayerRequestSettings {

    private final Set<String> timeouts = new HashSet<>();

    private final CorePlayer player;

    public PlayerRequestSettings(CorePlayer player){
        this.player = player;
    }

    public RequestFilter getRequestFilter(User user){
        return RequestFilter.valueOf(user.getConfigurations()
                .getOrDefault("requestFilter", new Configuration(RequestFilter.EVERYONE.toString())).getValue());
    }

    public int getRequestTimeout(User user){
        return Integer.parseInt(user.getConfigurations()
                .getOrDefault("requestTimeout", new Configuration("60")).getValue());
    }

    public void setRequestFilter(UserService service, User user, RequestFilter filter){
        user.getConfigurations().put("requestFilter", new Configuration(filter.toString()));
        service.update(user);
    }

    public void setRequestTimeout(UserService service, User user, int seconds){
        user.getConfigurations().put("requestTimeout", new Configuration("" + seconds));
        service.update(user);
    }

    public void checkAllowed(User user, User requester) throws RuntimeException{
        if(hasTimeout(requester.getLastName()))
            throw new RuntimeException("You must wait to send this player an request again");
        switch(getRequestFilter(user)){
            case FRIENDS:
                if(!user.getFriends().contains(requester))
                    throw new RuntimeException("This player is accepting requests from friends only");
            case NOBODY:
                throw new RuntimeException("This player does not accept requests");
        }
    }

    public boolean hasTimeout(String key){
        return timeouts.contains(key);
    }

    public void addTimeout(User user, String key){
        this.timeouts.add(key);
        ArematicsExecutor.asyncDelayed(() -> this.timeouts.remove(key), this.getRequestTimeout(user), TimeUnit.SECONDS);
    }

    public void removeTimeout(String key){
        this.timeouts.remove(key);
    }

    public void clearTimeouts(){
        this.timeouts.clear();
    }
}
