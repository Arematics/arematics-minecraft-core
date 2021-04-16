package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.server.entities.CurrencyEntity;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.service.ClanService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clan_member")
public class ClanMember implements Serializable, CurrencyEntity {

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

    public Clan getClan(ClanService service){
        return service.findClanById(getClanId());
    }

    public Optional<CorePlayer> online(){
        CorePlayer player = CorePlayer.get(Bukkit.getPlayer(uuid));
        return player == null ? Optional.empty() : Optional.of(player);
    }

    public Part prettyPrint(){
        String color = online().isPresent() ? "§4" : "§c";
        String name = Bukkit.getOfflinePlayer(getUuid()).getName();
        return PartBuilder.createHoverAndSuggest(color + name,
                "§aWatch stats of player " + name,
                "stats " + name);
    }

    public long getClanId(){
        return this.rank.getClanRankId().getClanId();
    }

    @Override
    public double getMoney() {
        if(!online().isPresent()) throw new RuntimeException("Not online");
        return online().get().getMoney();
    }

    @Override
    public void setMoney(double money) {
        if(!online().isPresent()) throw new RuntimeException("Not online");
        online().ifPresent(player -> player.setMoney(money));
    }

    @Override
    public void addMoney(double amount) {
        if(!online().isPresent()) throw new RuntimeException("Not online");
        online().ifPresent(player -> player.addMoney(amount));
    }

    @Override
    public void removeMoney(double amount) throws RuntimeException {
        if(!online().isPresent()) throw new RuntimeException("Not online");
        online().ifPresent(player -> player.removeMoney(amount));
    }
}
