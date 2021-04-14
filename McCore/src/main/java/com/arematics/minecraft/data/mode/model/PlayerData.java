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
@Table(name = "player_data")
public class PlayerData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "uuid", nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;
    @Column(name = "health", nullable = false)
    private double health;
    @Column(name = "hunger", nullable = false)
    private int hunger;
    @Column(name = "level", nullable = false)
    private int level;
    @Column(name = "xp", nullable = false)
    private float xp;

}
