package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.chat.controller.ChatThemeController;
import com.arematics.minecraft.data.global.model.ChatTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatThemeParser extends CommandParameterParser<ChatTheme> {

    private final ChatThemeController chatThemeController;

    @Autowired
    public ChatThemeParser(ChatThemeController chatThemeController) {
        this.chatThemeController = chatThemeController;
    }

    @Override
    public ChatTheme parse(String value) throws CommandProcessException {
            ChatTheme theme = chatThemeController.getTheme(value);
            if(theme == null) {
                throw new CommandProcessException("Theme not found");
            } else {
                return theme;
            }
    }
}
