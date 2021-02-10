package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.items.CoreItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

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
    private short durability;
    private short maxAmmo;
    private byte ammoPerLoading;
    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    private CoreItem[] weaponItem;
}
