package com.arematics.minecraft.core.language;

import com.arematics.minecraft.core.configurations.Config;
import com.arematics.minecraft.core.messaging.MessageHighlight;
import com.arematics.minecraft.core.server.Server;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class LanguageAPI {

    private final Server server;
    private final Map<String, Language> langs = new HashMap<>();
    private final Map<Player, LanguageUser> users = new HashMap<>();

    public LanguageUser getUser(Player p){
        if(!users.containsKey(p)) users.put(p, new LanguageUser(server.players().fetchPlayer(p)));
        return users.get(p);
    }

    public Language getLanguage(String key){
        return langs.get(key);
    }

    public String prepareMessage(CommandSender sender, MessageHighlight highlight, String key){
        if(sender instanceof Player){
            return Config.getInstance().getPrefix() +
                    highlight.getColorCode() +
                    getUser((Player)sender).getLanguage().getValue(key);
        }

        return Config.getInstance().getPrefix() +
                highlight.getColorCode() +
                langs.get("ENGLISH").getValue(key);
    }

    public String prepareRawMessage(CommandSender sender, String key){
        if(sender instanceof Player){
            return getUser((Player)sender).getLanguage().getValue(key);
        }

        return langs.get("ENGLISH").getValue(key);
    }
}
