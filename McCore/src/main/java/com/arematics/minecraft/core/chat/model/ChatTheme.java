package com.arematics.minecraft.core.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ChatTheme {

    private String[] placeholders = new String[]{"rank", "clan", "name", "chatMessage"};
    private String format = "%clan% - %rank% | %name%> %chatMessage%";

}
