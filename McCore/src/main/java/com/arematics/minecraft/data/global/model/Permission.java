package com.arematics.minecraft.data.global.model;

import com.arematics.minecraft.core.annotations.Perm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permission")
public class Permission implements Serializable {

    public static Permission of(Perm perm, String prefix){
        return new Permission(prefix + perm.permission(), perm.description());
    }

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "permission", nullable = false)
    private String permission;
    @Column(name = "description", nullable = false)
    private String description;

}
