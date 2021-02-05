package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "server_buff")
public class ServerBuff implements Serializable {
    @Id
    private String id;
    private String registeredType;
    private Timestamp endTime;
}
