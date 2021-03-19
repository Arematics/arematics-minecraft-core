package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.times.TimeUtils;
import com.arematics.minecraft.data.global.model.BukkitItemMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auction")
public class Auction implements Serializable, BukkitItemMapper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auctionId;
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID creator;
    private double startPrice;
    private double instantSell;
    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    private CoreItem[] sell;
    @OneToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private AuctionCategory auctionCategory;
    @Enumerated(EnumType.STRING)
    private AuctionType auctionType;
    private Timestamp endTime;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "auctionId", referencedColumnName = "auctionId")
    private Set<Bid> bids;

    @Override
    public CoreItem mapToItem() {
        return this.getSell()[0]
                .addToLore("§7Auction ID: §c" + this.getAuctionId())
                .addToLore("§7Start Bid Price: §c" + this.getStartPrice())
                .addToLore("§7Highest Bid Price: §c" + Collections.max(this.getBids()).getAmount())
                .addToLore("§7Ending in: §c" + TimeUtils.fetchEndDate(this.getEndTime()));
    }
}
