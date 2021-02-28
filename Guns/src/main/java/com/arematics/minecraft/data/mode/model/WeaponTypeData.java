package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.items.CoreItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "weapon_type_data")
public class WeaponTypeData implements Serializable {
    @Id
    @Enumerated(EnumType.STRING)
    private WeaponType weaponType;
    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    private CoreItem[] typeItem;
    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    private CoreItem[] ammunition;
}
