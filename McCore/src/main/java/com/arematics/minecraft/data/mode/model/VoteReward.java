package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.items.CoreItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vote_rewards")
public class VoteReward {
    @Id
    private String id;
    private int costs;
    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    @Column(name = "display_item", nullable = false)
    private CoreItem[] displayItem;
}
