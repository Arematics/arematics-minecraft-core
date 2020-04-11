package com.arematics.minecraft.core.model;

import com.arematics.model.Player;
import com.arematics.model.Players;
import lombok.Data;

import java.util.List;

@Data
public class PlayersImpl implements Players {
    private List<Player> players;
}
