package com.arematics.minecraft.data.mode.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ClanRankId implements Serializable {
    public static ClanRankId of(Long clanId, String name){
        return new ClanRankId(clanId, name);
    }
    @Column(name = "clan_id", nullable = false)
    private Long clanId;
    @Column(name = "rank_name", nullable = false)
    private String name;
}
