package com.arematics.minecraft.core.times;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    public static List<DayOfWeek> fromDaysString(String daysString){
        return Arrays.stream(daysString.split(",")).map(TimeUtils::fromDayString).collect(Collectors.toList());
    }

    private static DayOfWeek fromDayString(String day) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EE");
        TemporalAccessor accessor = formatter.parse(day);
        return DayOfWeek.from(accessor);
    }
}
