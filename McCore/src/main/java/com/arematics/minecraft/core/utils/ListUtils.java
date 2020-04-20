package com.arematics.minecraft.core.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

public class ListUtils {

    public static String getNameList(Class<? extends Enum<?>> e){
        return StringUtils.join(getNames(e), ", ");
    }

    public static String[] getNames(Class<? extends Enum<?>> e){
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
