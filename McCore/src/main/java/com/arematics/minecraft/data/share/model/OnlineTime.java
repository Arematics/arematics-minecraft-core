package com.arematics.minecraft.data.share.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "online_time")
public class OnlineTime {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID uuid;
    private Long time;
    private Long afk;
}
