package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "weapon")
public class Weapon {
    @Id
    private String id;
    @Enumerated(EnumType.STRING)
    private WeaponType type;
    private byte totalDamage;
    private byte bullets;
}
