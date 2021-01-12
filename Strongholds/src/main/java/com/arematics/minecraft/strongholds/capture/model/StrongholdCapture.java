package com.arematics.minecraft.strongholds.capture.model;

import com.arematics.minecraft.core.server.CorePlayer;
import com.arematics.minecraft.data.mode.model.Clan;
import com.arematics.minecraft.data.mode.model.Stronghold;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Data
@RequiredArgsConstructor
public class StrongholdCapture {
    private final Stronghold stronghold;
    private final long endTime;
    private final Set<CorePlayer> inStronghold = new HashSet<>();
    private final Set<CorePlayer> inStrongholdCapture = new HashSet<>();
    private final Map<Clan, Long> timings = new HashMap<>();
}
