package com.arematics.minecraft.data.mode.model;


import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.data.global.model.BukkitItemMapper;
import com.arematics.minecraft.data.global.model.BukkitMessageMapper;
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
public class Kit implements Serializable, BukkitMessageMapper, BukkitItemMapper {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "permission")
    private String permission;

    @Column(name = "cooldown", nullable = false)
    private Long cooldown;

    @Column(name = "min_play_time")
    private Long minPlayTime;

    @Type(type = "com.arematics.minecraft.data.types.CoreItemType")
    @Column(name = "display_item", nullable = false)
    private CoreItem[] displayItem;


    @Override
    public CoreItem mapToItem(Server server) {
        return this.getDisplayItem()[0]
                .bindCommand("kit " + this.getName())
                .closeInventoryOnClick()
                .setName("§8Kit: §c" + this.getName())
                .addToLore("§cClick to get kit");
    }

    @Override
    public MSG mapToMessage() {
        return new MSG(PartBuilder.createHoverAndSuggest(this.getName(),
                "Get kit " + this.getName(),
                "/kit " + this.getName()));
    }
}
