package com.arematics.minecraft.data.global.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rank_auction_perms")
public class RankAuctionPermissions {
    @Id
    private String rank;
    private short maxSells;
}
