package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clan")
public class Clan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "clan_name", nullable = false)
    private String name;

    @Column(name = "tag", nullable = false)
    private String tag;

    @Column(name = "color_code", nullable = false)
    private String colorCode;

    @Column(name = "slots", nullable = false)
    private byte slots;

    @Column(name = "kills", nullable = false)
    private Integer kills;

    @Column(name = "deaths", nullable = false)
    private Integer deaths;

    @Column(name = "coins", nullable = false)
    private Long coins;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, targetEntity = ClanRank.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "clan_id", updatable = false, insertable = false)
    private Set<ClanRank> ranks;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, targetEntity = ClanMember.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "clan_id", updatable = false, insertable = false)
    private Set<ClanMember> members;

    public Stream<Player> getAllOnline(){
        return this.getMembers().stream()
                .map(member -> Bukkit.getPlayer(member.getUuid()))
                .filter(Objects::nonNull);
    }
}
