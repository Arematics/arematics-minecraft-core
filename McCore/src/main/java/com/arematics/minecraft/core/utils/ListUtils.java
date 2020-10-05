package com.arematics.minecraft.core.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {

    public static String getNameList(Class<? extends Enum<?>> e){
        return StringUtils.join(getNames(e), ", ");
    }

    public static String[] getNames(Class<? extends Enum<?>> e){
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }

    public static String getNameListStartsWith(Class<? extends Enum<?>> e, String startsWith){
        return StringUtils.join(getNamesStartsWith(e, startsWith), ", ");
    }

    public static List<String> getNamesStartsWith(Class<? extends Enum<?>> e, String startsWith){
        return Arrays.stream(e.getEnumConstants()).filter(anEnum -> anEnum.name().startsWith(startsWith))
                .map(Enum::name).collect(Collectors.toList());
    }
}
