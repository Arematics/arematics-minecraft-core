package com.arematics.minecraft.core.proxy;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class MessagingUtils {

    public static void sendToServer(CorePlayer player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(player.getPlayer().getName());
        out.writeUTF(server);

        player.getPlayer().sendPluginMessage(Boots.getBoot(CoreBoot.class), "BungeeCord", out.toByteArray());
    }
}
