package com.arematics.minecraft.core.command;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CoreCommandTest extends Assertions {

    @Test
    void stringToStringArray() {
        String value = "Hallo ich bin Peter";
        String[] annotationValues = value.split(" ");
        System.out.println(annotationValues[0]);
    }
}
