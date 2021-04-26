package com.arematics.minecraft.core.server.entities.player;

import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RegionHandler {
    private final CorePlayer player;
    private final Set<ProtectedRegion> currentRegions = new HashSet<>();

    public boolean inRegionWithFlag(StateFlag flag){
        RegionQuery query = WorldGuardPlugin.inst().getRegionContainer().createQuery();
        return isFlagEnabled(query, flag);
    }

    public boolean isFlagEnabled(RegionQuery query, StateFlag flag){
        return query.testState(player.getLocation(), WorldGuardPlugin.inst().wrapPlayer(player.getPlayer()), flag);
    }
}
