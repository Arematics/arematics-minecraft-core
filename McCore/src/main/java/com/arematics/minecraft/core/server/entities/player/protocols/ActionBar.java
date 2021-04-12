package com.arematics.minecraft.core.server.entities.player.protocols;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ActionBar {
    private final CorePlayer player;

    public void sendActionBar(String message){
        PacketContainer init = new PacketContainer(PacketType.Play.Server.CHAT);
        init.getBytes().write(0, EnumWrappers.ChatType.GAME_INFO.getId());
        init.getChatComponents().write(0, WrappedChatComponent.fromText(message));
        player.packets().sendServerPacket(init);
    }
}
