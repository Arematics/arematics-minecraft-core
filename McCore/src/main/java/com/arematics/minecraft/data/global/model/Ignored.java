package com.arematics.minecraft.data.global.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ignored_player")
public class Ignored implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ignorer", nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID ignorer;
    @Column(name = "ignored", nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID ignored;
}
