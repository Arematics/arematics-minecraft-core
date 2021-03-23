package com.arematics.minecraft.core.times;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TimeUtils {

    public static String toString(Period period){
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendMonths().appendSuffix(" Months ")
                .appendWeeks().appendSuffix(" Weeks ")
                .appendDays().appendSuffix(" Days ")
                .minimumPrintedDigits(2)
                .appendHours().appendSuffix(" Hours ")
                .appendMinutes().appendSuffix(" Minutes ")
                .appendSeconds().appendSuffix(" Seconds ")
                .toFormatter();
        return formatter.print(period);
    }

    public static String toString(Long end){
        Duration duration = Duration.ofMillis(end - System.currentTimeMillis());
        return duration.isNegative() ? "None" : DurationFormatUtils.formatDurationWords(duration.toMillis(), true, true);
    }

    public static String toRawString(Long end){
        return DurationFormatUtils.formatDurationWords(end, true, true);
    }

    public static String toShortString(Period period){
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendMonths().appendSuffix(" MO ")
                .appendWeeks().appendSuffix(" W ")
                .appendDays().appendSuffix(" D ")
                .minimumPrintedDigits(2)
                .appendHours().appendSuffix(" H ")
                .appendMinutes().appendSuffix(" M ")
                .appendSeconds().appendSuffix(" S ")
                .toFormatter();
        return formatter.print(period);
    }

    public static String fetchEndDate(Timestamp timestamp){
        LocalDateTime time = timestamp.toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        Period period = Period.seconds((int) Duration.between(now, time).getSeconds()).normalizedStandard();
        return toString(period);
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

    public static DayOfWeek getToday(){
        return DayOfWeek.of(org.joda.time.LocalDateTime.now().getDayOfWeek());
    }

    public static DayOfWeek fromDayString(String day) {
        day = day.trim();
        final DateTimeFormatter dtf = new DateTimeFormatterBuilder( )
                .appendOptional(DateTimeFormatter.ofPattern("EEEE") )
                .appendOptional(DateTimeFormatter.ofPattern("E"))
                .toFormatter();
        TemporalAccessor accessor = dtf.parse(day);
        return DayOfWeek.from(accessor);
    }
}
