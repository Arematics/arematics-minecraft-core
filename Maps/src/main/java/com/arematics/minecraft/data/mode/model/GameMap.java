package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "maps")
public class GameMap {
    @Id
    private String id;
    @Type(type = "com.arematics.minecraft.data.types.LocationType")
    @Basic(fetch = FetchType.EAGER)
    private Location location;
}
