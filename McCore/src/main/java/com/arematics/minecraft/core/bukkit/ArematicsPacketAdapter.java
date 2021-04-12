package com.arematics.minecraft.core.bukkit;

import com.arematics.minecraft.core.server.ArematicsComponent;
import com.arematics.minecraft.core.server.Server;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public abstract class ArematicsPacketAdapter extends ArematicsComponent {
    public static final String TEAM_MESSAGE = "§6CustomPayload§8§l |§5Player %d§n%name%§5 has shown a §lvery§5 " +
            "strange behavior! Possibly wanted to crash the server! §6Reason: §o%reason% §eEventually watch and / or ban!";

    private final PacketType type;

    public ArematicsPacketAdapter(Server server, PacketType type){
        super(server);
        this.type = type;
        load();
    }

    @Override
    protected void load() {
        PacketAdapter adapter = new PacketAdapter(plugin, type) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if(event.isCancelled()) return;
                onReceive(event);
            }
        };
        protocolManager.addPacketListener(adapter);
    }

    protected abstract void onReceive(final PacketEvent event);
}
