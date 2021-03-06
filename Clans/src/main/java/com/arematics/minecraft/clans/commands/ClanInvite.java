package com.arematics.minecraft.clans.commands;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.Clan;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ClanInvite {
    private final CorePlayer inviter;
    private final Clan clan;
}
