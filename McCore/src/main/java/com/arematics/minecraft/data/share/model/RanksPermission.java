package com.arematics.minecraft.data.share.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(RankPermId.class)
@Table(name = "ranks_permission")
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
