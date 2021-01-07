package com.arematics.minecraft.core.messaging.advanced;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Format {

    BOLD("bold", 'l'),
    ITALIC("italic", 'o'),
    UNDERLINED("underlined", 'n'),
    STRIKETHROUGH("strikethrough", 'm'),
    OBFUSCATED("obfuscated", 'k');

    public final String FORMAT;
    public final char STYLE_CODE;
}
