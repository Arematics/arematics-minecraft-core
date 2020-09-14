package com.arematics.minecraft.core.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "permission", nullable = false)
    private String permission;
    @Column(name = "description", nullable = false)
    private String description;

}
