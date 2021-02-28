package com.arematics.minecraft.data.share.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CooldownKey.class)
@Table(name = "cooldown")
public class Cooldown implements Serializable {
    @Id
    private String id;
    @Id
    private String secondKey;
    @Column(name = "end_time", nullable = false)
    private Long endTime;
}
