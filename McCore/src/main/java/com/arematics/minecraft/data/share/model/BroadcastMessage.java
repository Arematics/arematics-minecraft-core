package com.arematics.minecraft.data.share.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "broadcast_messages")
public class BroadcastMessage implements Serializable {
    @Id
    private Long id;
    @Column(name = "message_key", nullable = false)
    private String messageKey;
}
