package com.arematics.minecraft.data.mode.model;

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
public class Home implements Serializable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID owner;
    @Id
    private String name;
    @Type(type = "com.arematics.minecraft.data.types.LocationType")
    private Location location;
    private Timestamp created;
}
