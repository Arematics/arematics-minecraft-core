package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.events.CurrencyEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "currency_statistic_data")
@IdClass(CurrencyDataId.class)
public class CurrencyData implements Serializable {
    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID executor;
    @Id
    private Timestamp executed;
    private double amount;
    @Enumerated(EnumType.STRING)
    private CurrencyEventType type;
    private String target;
    private boolean success;
}
