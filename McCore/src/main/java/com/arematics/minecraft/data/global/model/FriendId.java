package com.arematics.minecraft.data.global.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class FriendId implements Serializable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID targetUuid;
}
