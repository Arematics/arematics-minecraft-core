package com.arematics.minecraft.core.bukkit;

import com.arematics.minecraft.core.listener.Quitable;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.StreamSerializer;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomPayloadFixer extends ArematicsPacketAdapter implements Quitable {
    private final Map<Player, Long> PACKET_USAGE = new ConcurrentHashMap<>();
    private static final String kickMessage =
            "§8<==================== §cSoulPvP §8====================>\n" +
                    "§cYour client is behaving very strangely. Please refrain from doing so.\n" +
                    "§8<=======================================================>";
    private static final String teamMessage = "§6CustomPayload§8§l |§5Player %d§n%name%§5 has shown a §lvery§5 strange " +
            "behavior! Possibly wanted to crash the server! §6Reason: §o%reason% §eEventually watch and / or ban!";

    @Autowired
    public CustomPayloadFixer(Server server){
        super(server, PacketType.Play.Client.CUSTOM_PAYLOAD);
    }

    @Override
    public void load() {
        super.load();
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            Iterator<Map.Entry<Player, Long>> iterator;
            Player player;

            @Override
            public void run() {
                iterator = PACKET_USAGE.entrySet().iterator();
                while (iterator.hasNext()) {
                    player = iterator.next().getKey();
                    if (!player.isOnline() || !player.isValid()) {
                        iterator.remove();
                    }
                }
            }
        }, 20L, 20L);
    }

    @Override
    protected void onReceive(PacketEvent event) {
        checkPacket(event);
    }

    private void checkPacket(final PacketEvent event) {
        final Player player = event.getPlayer();
        final long lastPacket = PACKET_USAGE.getOrDefault(player, -1L);
        if (lastPacket == -2L) {
            event.setCancelled(true);
            return;
        }
        final String name = event.getPacket().getStrings().readSafely(0);
        if (!"MC|BSign".equals(name) && !"MC|BEdit".equals(name) && !"REGISTER".equals(name)) {
            return;
        }
        try {
            if ("REGISTER".equals(name)) {
                checkChannels(event);
            }
            else {
                if (!elapsed(lastPacket)) {
                    throw new IOException("Packet flood");
                }
                PACKET_USAGE.put(player, System.currentTimeMillis());
                checkNbtTags(event);
            }
        }
        catch (Throwable ex) {
            PACKET_USAGE.put(player, -2L);
            server.schedule().runSync(() -> {
                player.kickPlayer(kickMessage);
                String message = teamMessage.replace("%name%", player.getName()).replace("%reason%", ex.getMessage());
                System.err.println("[CustomPayloadFixer] Blocked expoit from " + player.getName() + ": " + ex.getMessage());
                server.getOnlineTeam().stream()
                        .filter(result -> result.hasPermission("team.crashmessages"))
                        .forEach(result -> result.info(message).handle());
            });
            plugin.getLogger()
                    .warning(player.getName() + " tried to exploit CustomPayload: " + ex.getMessage());
            event.setCancelled(true);
        }
    }

    private void checkNbtTags(final PacketEvent event) throws IOException {
        final PacketContainer container = event.getPacket();
        final ByteBuf buffer = ((ByteBuf)container.getSpecificModifier((Class)ByteBuf.class).read(0)).copy();
        final byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        try (final DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(bytes))) {
            final ItemStack itemStack = StreamSerializer.getDefault().deserializeItemStack(inputStream);
            if (itemStack == null) {
                throw new IOException("Unable to deserialize ItemStack");
            }
            final NbtCompound root = (NbtCompound) NbtFactory.fromItemTag(itemStack);
            if (root == null) {
                throw new IOException("No NBT tag?!");
            }
            if (!root.containsKey("pages")) {
                throw new IOException("No 'pages' NBT compound was found");
            }
            final NbtList<String> pages = root.getList("pages");
            if (pages.size() > 50) {
                throw new IOException("Too much pages (" + pages.size() + ")");
            }
        }
        finally {
            buffer.release();
        }
    }

    private void checkChannels(final PacketEvent event) throws Exception {
        int channelsSize = event.getPlayer().getListeningPluginChannels().size();
        final PacketContainer container = event.getPacket();
        final ByteBuf buffer = ((ByteBuf)container.getSpecificModifier((Class)ByteBuf.class).read(0)).copy();
        try {
            for (int i = 0; i < buffer.toString(Charsets.UTF_8).split("\u0000").length; ++i) {
                if (++channelsSize > 124) {
                    throw new IOException("Too much channels");
                }
            }
        }
        finally {
            buffer.release();
        }
    }

    private boolean elapsed(final long from) {
        return from == -1L || System.currentTimeMillis() - from > (long) 100;
    }

    @Override
    public void quit(Player player) {
        PACKET_USAGE.remove(player);
    }
}
