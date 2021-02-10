package com.arematics.minecraft.data.share.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cooldown")
public class Cooldown implements Serializable {

    @EmbeddedId
    private CooldownKey cooldownKey;
    @Column(name = "end_time", nullable = false)
    private Long endTime;
}
