package com.arematics.minecraft.data.mode.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.DayOfWeek;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ShTimeId implements Serializable {
    @Id
    @Column(name = "stronghold_name")
    private String strongholdName;

    @Id
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;
}
