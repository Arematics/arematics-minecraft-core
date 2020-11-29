package com.arematics.minecraft.core.language;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.global.model.Configuration;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import org.bukkit.entity.Player;

public class LanguageUser {

    private final CorePlayer player;
    private Language language;

    public LanguageUser(CorePlayer player){
        this.player = player;
        Configuration configuration = this.player.getUser().getConfigurations().get("language");
        if(configuration != null) this.language = LanguageAPI.getLanguage(configuration.getValue());
        else setLanguage(LanguageAPI.getLanguage("ENGLISH"));
    }

    public Player getPlayer() {
        return player.getPlayer();
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        Configuration configuration = this.player.getUser().getConfigurations().get("language");
        if(configuration == null) configuration = new Configuration(language.getName());
        User user = this.player.getUser();
        user.getConfigurations().put("language", configuration);
        UserService service = Boots.getBoot(CoreBoot.class).getContext().getBean(UserService.class);
        service.update(user);
        this.language = language;
    }
}
