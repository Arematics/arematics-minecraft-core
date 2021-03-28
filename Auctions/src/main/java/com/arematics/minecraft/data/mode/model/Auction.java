package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.items.ItemCategory;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.data.global.model.BukkitItemMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auction")
public class Auction implements Serializable, BukkitItemMapper {

    private static final long serialVersionUID = 3235234525L;

    public static Long readAuctionIdFromItem(CoreItem item){
        if(!item.getMeta().hasKey("auctionId")) return -1L;
        return Long.parseLong(item.getMeta().getString("auctionId"));
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionId;
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID creator;
    private double startPrice;
    private double instantSell;
    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    private CoreItem[] sell;
    @Enumerated(EnumType.STRING)
    private ItemCategory itemCategory;
    @Enumerated(EnumType.STRING)
    private AuctionType auctionType;
    private Timestamp endTime;
    private boolean sold;
    private boolean ownerCollected;
    private boolean bidCollected;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "auctionId", referencedColumnName = "auctionId")
    private Set<Bid> bids;

    public double highestBidPrice(){
        return this.getBids().isEmpty() ? this.getStartPrice() : Collections.max(this.getBids()).getAmount();
    }

    public boolean ended(){
        return this.isSold() || this.getEndTime().before(Timestamp.valueOf(LocalDateTime.now()));
    }

    public String topBid(){
        if(!this.getBids().isEmpty()){
            Bid bid = Collections.max(this.getBids());
            return Bukkit.getOfflinePlayer(bid.getBidder()).getName();
        }
        return "Nobody";
    }

    @Override
    public CoreItem mapToItem(Server server) {
        double bits = this.getBids().isEmpty() ? this.getStartPrice() : Collections.max(this.getBids()).getAmount();
        String ending = ended() ? "§aEnded" : "§8Ending in: §e" + TimeUtils.fetchEndDate(this.getEndTime());
        CoreItem item = this.getSell()[0]
                .setString("auctionId", String.valueOf(this.getAuctionId()))
                .addToLore(" ");
        if(bits != 0)
            item.addToLore(" ", "§8Top Bid: §a" + topBid()).addToLore("§8Current Bid Price: §e" + bits + " Coins");
        if(instantSell != 0) item.addToLore("", "§8Instant Buy Price: §e" + this.getInstantSell() + " Coins");
        item.addToLore(" ", ending);
        return item;
    }
}
