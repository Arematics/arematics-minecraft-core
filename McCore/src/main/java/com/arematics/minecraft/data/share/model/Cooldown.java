package com.arematics.minecraft.data.share.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cooldown")
public class Cooldown {

    @EmbeddedId
    private CooldownKey cooldownKey;
    @Column(name = "end_time", nullable = false)
    private Long endTime;
}
