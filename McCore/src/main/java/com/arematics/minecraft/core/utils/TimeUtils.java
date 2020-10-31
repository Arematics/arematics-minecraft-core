package com.arematics.minecraft.core.utils;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

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

    public static long toTicks(long time, TimeUnit unit){
        return unit.toMillis(time) / 50;
    }
}
