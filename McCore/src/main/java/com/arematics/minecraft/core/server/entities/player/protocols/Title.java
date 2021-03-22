package com.arematics.minecraft.core.server.entities.player.protocols;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Title {
    private final CorePlayer player;

    public void sendTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        sendTitle(EnumWrappers.TitleAction.TITLE, title, fadeIn, stay, fadeOut);
        sendTitle(EnumWrappers.TitleAction.SUBTITLE, subTitle, fadeIn, stay, fadeOut);
    }

    public void sendTitle(EnumWrappers.TitleAction action, String message, int fadeIn, int stay, int fadeOut) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.TITLE);
        packet.getTitleActions().write(0, action);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(message));
        packet.getIntegers().write(0, fadeIn);
        packet.getIntegers().write(1, stay);
        packet.getIntegers().write(2, fadeOut);

        player.getPackets().sendServerPacket(packet);
    }
}
