package com.arematics.minecraft.core.messaging;

public interface MessageHighlightType {
    MessageReplacement WARNING();
    MessageReplacement FAILURE();
    MessageReplacement skip();
}
