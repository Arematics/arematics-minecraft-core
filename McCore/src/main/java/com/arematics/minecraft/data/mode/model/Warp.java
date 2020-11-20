package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Warp {

    private String name;
    private Location location;

}
