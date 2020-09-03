package com.arematics.minecraft.core.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private UUID uuid;
    private UUID soulConnection;
    private boolean verified;
}
