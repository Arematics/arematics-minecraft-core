package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.items.CoreItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "weapon_type_data")
public class WeaponTypeData {
    @Id
    private WeaponType weaponType;
    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    private CoreItem[] typeItem;
}
