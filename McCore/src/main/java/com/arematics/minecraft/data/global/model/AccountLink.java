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
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AccountLinkId.class)
@Table(name = "account_links")
public class AccountLink implements Serializable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID userOne;
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID userTwo;
    private String linkStrategy;
    private Timestamp linkTime;
}
