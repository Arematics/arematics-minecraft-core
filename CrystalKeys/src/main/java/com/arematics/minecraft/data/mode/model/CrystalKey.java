package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.utils.CommandUtils;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "crystal_key")
public class CrystalKey {
    @Id
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "colorCode", nullable = false)
    private String colorCode;

    @Transient
    private final String prettyPrint = CommandUtils.prettyReplace("Crystal Key", getName());

    public String getTotalName(){
        return this.getColorCode() + this.getName();
    }
}
