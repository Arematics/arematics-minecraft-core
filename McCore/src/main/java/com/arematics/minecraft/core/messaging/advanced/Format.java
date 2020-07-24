package com.arematics.minecraft.core.messaging.advanced;

public enum Format {

    BOLD("bold", 'l'),
    ITALIC("italic", 'o'),
    UNDERLINED("underlined", 'n'),
    STRIKETHROUGH("strikethrough", 'm'),
    OBFUSCATED("obfuscated", 'k');

    public final String FORMAT;
    public final char STYLE_CODE;

    Format(String format, char stylecode){
        this.FORMAT = format;
        this.STYLE_CODE = stylecode;
    }
}
