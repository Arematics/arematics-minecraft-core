package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class PlayerBuffId implements Serializable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;
    @Id
    private String potionEffectType;
}
