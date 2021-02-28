package com.arematics.minecraft.data.mode.model;



import com.arematics.minecraft.core.items.CoreItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "kit")
public class Kit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "permission", nullable = true)
    private String permission;

    @Column(name = "cooldown", nullable = false)
    private Long cooldown;

    @Column(name = "min_play_time")
    private Long minPlayTime;

    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    @Column(name = "display_item", nullable = false)
    private CoreItem[] displayItem;


}
