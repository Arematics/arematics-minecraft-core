package com.arematics.minecraft.data.mode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.time.DayOfWeek;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(ShTimeId.class)
@Table(name = "stronghold_timings")
public class StrongholdTime {
    @Id
    @Column(name = "stronghold_name")
    private String strongholdName;

    @Id
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Column(name = "time")
    private Time time;
}
