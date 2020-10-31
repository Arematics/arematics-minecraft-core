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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventories")
public class InventoryData {
    @Id
    private Long id;
    private String key;
    private String title;
    private byte slots;
    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    private CoreItem[] items;
}
