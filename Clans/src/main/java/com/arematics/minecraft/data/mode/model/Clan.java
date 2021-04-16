package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@Getter
@Setter
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
    private double coins;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, targetEntity = ClanRank.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "clan_id", updatable = false, insertable = false)
    private Set<ClanRank> ranks;

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, targetEntity = ClanMember.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "clan_id", updatable = false, insertable = false)
    private Set<ClanMember> members;

    public String readInformation() throws CommandProcessException{
        return  "   \r" +
                "   §7Tag: %clan_tag%" + "\n" +
                "   §7Owner: %clan_owner%"  + "\n" +
                "   §7Kills: %clan_kills%" + "\n" +
                "   §7Deaths: %clan_deaths%" + "\n" +
                "   §7Coins: %clan_coins%" + "\n" +
                "   §7Slots: %clan_slots%" + "\n" +
                "   §7Members: %members_list%";

    }

    public ClanMember findOwner(){
        return members.stream().filter(member -> member.getRank().getRankLevel().equals(0))
                .findFirst()
                .orElseThrow(() -> new CommandProcessException("Clan Owner could not be found"));
    }

    public Stream<CorePlayer> getAllOnline(){
        return this.getMembers().stream()
                .map(member -> Bukkit.getPlayer(member.getUuid()))
                .filter(Objects::nonNull)
                .map(CorePlayer::get);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clan other = (Clan) o;
        return other.getId().equals(this.getId());
    }

    @Override
    public int hashCode(){
        return this.getId().hashCode() + this.getName().hashCode();
    }
}
