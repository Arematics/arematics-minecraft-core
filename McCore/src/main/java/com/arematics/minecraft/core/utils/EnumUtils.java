package com.arematics.minecraft.core.utils;

import java.util.List;

public class EnumUtils {


    public static <E extends Enum<E>> E getNext(E enumValue){
        Class<E> enumClass = enumValue.getDeclaringClass();
        List<E> types = org.apache.commons.lang3.EnumUtils.getEnumList(enumClass);
        int index = enumValue.ordinal();
        if(index == types.size() - 1) index = 0;
        else index += 1;
        return types.get(index);
    }
}
