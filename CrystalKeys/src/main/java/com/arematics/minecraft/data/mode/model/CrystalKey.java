package com.arematics.minecraft.data.mode.model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "crystal_key")
public class CrystalKey {
    @Id
    @Column(name = "crystal_name", nullable = false)
    private String name;
    @Column(name = "colorCode", nullable = false)
    private String colorCode;
    @Type(type = "com.arematics.minecraft.data.types.ColorType")
    private Color color;

    public String getTotalName(){
        return this.getColorCode() + this.getName();
    }
}
