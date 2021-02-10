package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "clan_permission")
public class ClanPermission implements Serializable {
    public static ClanPermission of(String value){
        return new ClanPermission(value);
    }

    @Id
    private String permission;
}
