package com.arematics.minecraft.core.server;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public abstract class ArematicsComponent {

    public final Server server;
    public final CoreBoot plugin;
    public final ProtocolManager protocolManager;

    public ArematicsComponent(Server server){
        this.server = server;
        this.plugin = Boots.getBoot(CoreBoot.class);
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        plugin.getLogger().info(this.getClass().getSimpleName() + " enabled");
    }

    protected abstract void load();
}
