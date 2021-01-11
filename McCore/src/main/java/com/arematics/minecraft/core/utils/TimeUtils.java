package com.arematics.minecraft.core.utils;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String toString(Period period){
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays().appendSuffix(" Days")
                .appendHours().appendSuffix(" Hours")
                .appendMinutes().appendSuffix(" Minutes")
                .appendSeconds().appendSuffix(" Seconds")
                .toFormatter();
        return formatter.print(period);
    }

    public static String toShortString(Period period){
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays().appendSuffix(" D ")
                .appendHours().appendSuffix(" H ")
                .appendMinutes().appendSuffix(" M ")
                .appendSeconds().appendSuffix(" S ")
                .toFormatter();
        return formatter.print(period);
    }

    public static long toTicks(long time, TimeUnit unit){
        return unit.toMillis(time) / 50;
    }

    public static LocalDateTime toLocalDateTime(Period period){
        long millis = period.toStandardDuration().getMillis();
        return LocalDateTime.now().plus(millis, ChronoUnit.MILLIS);
    }
}
