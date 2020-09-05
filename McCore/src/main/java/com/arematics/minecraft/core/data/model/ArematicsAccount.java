package com.arematics.minecraft.core.data.model;

import com.arematics.minecraft.core.items.CoreItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "arematics_account", schema = "arematics")
public class ArematicsAccount {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID soulConnection;
    private boolean verified;
    @Type(type = "com.arematics.minecraft.core.data.types.CoreItemType")
    private CoreItem[] test;
}
