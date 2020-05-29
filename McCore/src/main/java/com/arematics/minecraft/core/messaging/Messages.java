package com.arematics.minecraft.core.messaging;

public class Messages {

    /**
     * Create new Message with Fluent Message Builder
     * @param value Value for the message use message if no Language Value is found
     * @return MessageHighlight Builder Class
     */
    public static MessageHighlightType create(String value){
        return Message.create(value);
    }
}
