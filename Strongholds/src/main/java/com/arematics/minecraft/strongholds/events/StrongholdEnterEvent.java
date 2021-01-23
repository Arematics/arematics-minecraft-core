package com.arematics.minecraft.strongholds.events;

import com.arematics.minecraft.core.events.BaseEvent;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.Stronghold;
import com.arematics.minecraft.ri.MovementWay;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString(callSuper = true)
public class StrongholdEnterEvent extends BaseEvent {

    private final ProtectedRegion region;
    private final CorePlayer player;
    private final MovementWay movement;
    private final Stronghold stronghold;
}
