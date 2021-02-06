package com.arematics.minecraft.auctions.handler;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.AuctionCategory;
import lombok.Data;

@Data
public class PlayerAuctionHandler {
    private final CorePlayer player;
    private AuctionCategory category = null;
    private AuctionType type = AuctionType.ALL;
    private AuctionSort sort = AuctionSort.HIGHEST_BID;
    private String search = "";
}
