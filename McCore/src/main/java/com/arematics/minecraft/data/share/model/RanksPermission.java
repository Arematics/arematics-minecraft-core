package com.arematics.minecraft.data.share.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "ranks_permission")
@IdClass(RankPermId.class)
public class RanksPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Id
    @Column(name = "permission", nullable = false)
    private String permission;

    @Column(name = "until")
    private Timestamp until;

}
