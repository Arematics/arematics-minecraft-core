package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clan_ranks")
public class ClanRank implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ClanRankId clanRankId;

    @Column(name = "color_code", nullable = false)
    private String colorCode;

    @Column(name = "rank_level")
    private Integer rankLevel;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "clan_rank_permission",
            joinColumns = {@JoinColumn(name = "clan_id"), @JoinColumn(name = "rank_name")},
            inverseJoinColumns = @JoinColumn(name = "permission"))
    private List<ClanPermission> permissions;
}
