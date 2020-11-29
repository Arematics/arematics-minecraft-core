package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clan_member")
public class ClanMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "uuid", nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;

    @OneToOne
    @JoinColumns({@JoinColumn(name = "clan_id"), @JoinColumn(name = "clan_rank")})
    private ClanRank rank;

    @Column(name = "clan_kills", nullable = false)
    private Integer clanKills;

    @Column(name = "clan_deaths", nullable = false)
    private Integer clanDeaths;

}