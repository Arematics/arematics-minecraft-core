package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.items.CoreItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auction_category")
public class AuctionCategory implements Serializable {
    @Id
    private String categoryId;
    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    private CoreItem[] displayItem;
    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    private CoreItem[] items;
}
