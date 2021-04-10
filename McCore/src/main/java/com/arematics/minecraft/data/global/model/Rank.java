package com.arematics.minecraft.data.global.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

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
    @Column(name = "in_team", nullable = false)
    private boolean inTeam;
    @Column(name = "sort_char", nullable = false)
    private String sortChar;
    @Column(name = "last_change", nullable = false)
    private Timestamp lastChange;

    public boolean isOver(Rank target){
        return sortChar.compareToIgnoreCase(target.getSortChar()) < 0;
    }

    public boolean isSameOrHigher(Rank target){
        return sortChar.compareToIgnoreCase(target.getSortChar()) <= 0;
    }
}
