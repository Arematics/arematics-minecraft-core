package com.arematics.minecraft.core.messaging.advanced;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum JsonColor {

    WHITE("white", 'f'),
    BLACK("black", '0'),
    YELLOW("yellow", 'e'),
    GOLD("gold", '6'),
    AQUA("aqua", 'b'),
    DARK_AQUA("dark_aqua", '3'),
    BLUE("blue", '9'),
    DARK_BLUE("dark_blue", '1'),
    LIGHT_PURPLE("light_purple", 'd'),
    DARK_PURPLE("dark_purple", '5'),
    RED("red", 'c'),
    DARK_RED("dark_red", '4'),
    GREEN("green", 'a'),
    DARK_GREEN("dark_green", '2'),
    GRAY("gray", '7'),
    DARK_GRAY("dark_gray", '8');

    public final String NAME;
    public final char COLOR_CODE;

    public static JsonColor findByName(String name, JsonColor orElse){
        return Arrays.stream(values()).filter(color -> color.NAME.equalsIgnoreCase(name))
                .findFirst().orElse(orElse);
    }
}
