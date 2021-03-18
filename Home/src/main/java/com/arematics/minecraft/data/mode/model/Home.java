package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.messaging.advanced.MSG;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.messaging.advanced.PartBuilder;
import com.arematics.minecraft.data.global.model.BukkitMessageMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(HomeId.class)
@Table(name = "homes")
public class Home implements Serializable, BukkitMessageMapper {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID owner;
    @Id
    private String name;
    @Type(type = "com.arematics.minecraft.data.types.LocationType")
    private Location location;
    private Timestamp created;

    @Override
    public MSG mapToMessage() {
        Part teleport = PartBuilder.createHoverAndRun("§a" + this.getName(),
                "§aTeleport to home " + this.getName(),
                "/home " + this.getName());
        Part delete = PartBuilder.createHoverAndSuggest(" §8[§cX§8]",
                "§cDelete home " + this.getName(),
                "/delhome "+ this.getName());
        return new MSG(teleport, delete);
    }
}
