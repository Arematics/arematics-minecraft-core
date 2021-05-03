package com.arematics.minecraft.data.global.model;

import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.*;
import org.bukkit.Bukkit;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Entity
@Audited
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "arematics_connection", nullable = false)
    private UUID arematicsConnection;
    @Id
    @Column(name = "uuid", nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "last_join")
    private Timestamp lastJoin;
    @OneToOne
    @JoinColumn(name = "rank", referencedColumnName = "id")
    private Rank rank;
    @OneToOne
    @JoinColumn(name = "display_rank", referencedColumnName = "id")
    private Rank displayRank;
    @Column(name = "user_karma", nullable = false)
    private int karma;
    private String currentServer;
    @NotAudited
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_configurations", joinColumns = @JoinColumn(name = "uuid"))
    @MapKeyColumn(name = "name")
    private Map<String, Configuration> configurations;

    public Optional<CorePlayer> online(Server server){
        CorePlayer player = server.players().fetchPlayer(Bukkit.getPlayer(uuid));
        return player == null ? Optional.empty() : Optional.of(player);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uuid, user.uuid);
    }

    @Override
    public int hashCode(){
        return Objects.hash(uuid);
    }
}
