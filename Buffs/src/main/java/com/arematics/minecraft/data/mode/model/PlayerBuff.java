package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PlayerBuffId.class)
@Table(name = "buff")
public class PlayerBuff implements Serializable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;
    @Id
    private String potionEffectType;
    private byte strength;
    private Timestamp endTime;
    private boolean active;
}
