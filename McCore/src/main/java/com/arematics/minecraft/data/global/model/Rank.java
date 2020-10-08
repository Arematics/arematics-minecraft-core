package com.arematics.minecraft.data.global.model;

import com.arematics.minecraft.data.share.model.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.WhereJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Audited
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ranks")
public class Rank implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "short_name", nullable = false)
    private String shortName;
    @Column(name = "color_code", nullable = false)
    private String colorCode;
    @Column(name = "last_change", nullable = false)
    private Timestamp lastChange;
    @NotAudited
    @WhereJoinTable(clause = "mode = '' AND until IS NULL OR until > NOW()")
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ranks_permission", joinColumns = {@JoinColumn(name = "id")},
            inverseJoinColumns = { @JoinColumn(name = "permission")})
    private Set<Permission> permissions;
}
