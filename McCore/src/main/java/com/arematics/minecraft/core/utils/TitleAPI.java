package com.arematics.minecraft.core.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class TitleAPI {
    public static void sendTitle(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        sendTitle(player, title, fadeIn, stay, fadeOut);
        sendSubtitle(player, subTitle, fadeIn, stay, fadeOut);
    }

    public static void sendTitle(Player player, String title, int fadeIn, int stay, int fadeOut) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.TITLE);
        packet.getTitleActions().write(0, EnumWrappers.TitleAction.TITLE);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(title));
        packet.getIntegers().write(0, fadeIn);
        packet.getIntegers().write(1, stay);
        packet.getIntegers().write(2, fadeOut);

        sendTitlePacket(player, packet);
    }

    public static void sendSubtitle(Player player, String subTitle, int fadeIn, int stay, int fadeOut) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.TITLE);
        packet.getTitleActions().write(0, EnumWrappers.TitleAction.SUBTITLE);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(subTitle));
        packet.getIntegers().write(0, fadeIn);
        packet.getIntegers().write(1, stay);
        packet.getIntegers().write(2, fadeOut);

        sendTitlePacket(player, packet);
    }

    private static void sendTitlePacket(Player player, PacketContainer container) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
