package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "player_game_stats")
public class GameStats implements Serializable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;
    @Column(name = "kills", nullable = false)
    private int kills;
    @Column(name = "deaths", nullable = false)
    private int deaths;
    private Integer bounty;
    @Column(name = "coins", nullable = false)
    private Double coins;
}
