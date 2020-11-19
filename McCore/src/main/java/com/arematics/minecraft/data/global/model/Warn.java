package com.arematics.minecraft.data.global.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "warns")
public class Warn implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "uuid", nullable = false)
    private UUID uuid;

    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "warned_by", nullable = false)
    private UUID warnedBy;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "warned_time", nullable = false)
    private Timestamp warnedTime;

}
