package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "command_entity")
public class CommandEntity implements Serializable {

    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(name = "uuid", nullable = false)
    private UUID uuid;
    @Column(name = "cmd", nullable = false)
    private String bindedCommand;
}
