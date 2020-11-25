package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "warps")
public class Warp {

    @Id
    @Column(name = "warp_name", nullable = false)
    private String name;
    @Column(name = "location", nullable = false)
    @Type(type = "com.arematics.minecraft.data.types.LocationType")
    @Basic(fetch = FetchType.EAGER)
    private Location location;

}
