package com.arematics.minecraft.core.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_permission")
public class UserPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "uuid", nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;
    @ManyToOne
    @JoinColumn(name = "permission")
    private Permission permission;
    private Timestamp until;

}
