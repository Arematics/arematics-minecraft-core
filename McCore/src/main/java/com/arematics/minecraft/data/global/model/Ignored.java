package com.arematics.minecraft.data.global.model;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.items.Items;
import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ignored_player")
public class Ignored implements Serializable, BukkitMessageMapper, BukkitItemMapper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ignorer", nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID ignorer;
    @Column(name = "ignored", nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID ignored;

    @Override
    public CoreItem mapToItem(Server server) {
        String name = Bukkit.getOfflinePlayer(this.getIgnored()).getName();
        return Items.fetchPlayerSkull(name)
                .bindCommand("ignore rem " + name)
                .setName("§8Player: §c" + name)
                .addToLore("§cClick to unignore player");
    }

    @Override
    public MSG mapToMessage() {
        String name = Bukkit.getOfflinePlayer(this.getIgnored()).getName();
        return new MSG(PartBuilder.createHoverAndSuggest(name, "§7Unignore §c" + name,
                "/ignore rem " + name));
    }
}
