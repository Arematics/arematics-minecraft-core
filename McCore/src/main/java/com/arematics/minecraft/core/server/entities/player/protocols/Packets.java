package com.arematics.minecraft.core.server.entities.player.protocols;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@RequiredArgsConstructor
public class Packets {
    private final Player player;

    public void sendServerPacket(PacketContainer container) {
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
