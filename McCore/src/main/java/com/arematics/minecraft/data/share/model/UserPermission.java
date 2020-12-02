package com.arematics.minecraft.data.share.model;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "user_permission")
@Data
@IdClass(UserPermId.class)
public class UserPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "uuid", nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;
    @Id
    @Column(name = "permission", nullable = false)
    private String permission;

    @Column(name = "until")
    private Timestamp until;

}
