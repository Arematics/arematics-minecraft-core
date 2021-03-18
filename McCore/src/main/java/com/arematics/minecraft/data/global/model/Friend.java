package com.arematics.minecraft.data.global.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@IdClass(FriendId.class)
@Table(name = "user_friends")
public class Friend implements Serializable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID targetUuid;
}
