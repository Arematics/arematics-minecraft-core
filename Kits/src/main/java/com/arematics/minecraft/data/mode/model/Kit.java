package com.arematics.minecraft.data.mode.model;



import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.data.share.model.Permission;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "kit")
public class Kit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne
    private Permission permission;

    @Column(name = "cooldown", nullable = false)
    private Long cooldown;

    @Column(name = "min_play_time")
    private Long minPlayTime;

    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    @Column(name = "display_item", nullable = false)
    private CoreItem[] displayItem;

    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    @Column(name = "content", nullable = false)
    private CoreItem[] content;

}
