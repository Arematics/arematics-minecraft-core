package com.arematics.minecraft.core.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity
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
    @WhereJoinTable(clause = "until IS NULL OR until > NOW()")
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ranks_permission", joinColumns = {@JoinColumn(name = "id")},
            inverseJoinColumns = { @JoinColumn(name = "permission")})
    private Set<Permission> permissions;
}
