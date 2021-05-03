package com.arematics.minecraft.data.service;

public interface MessageReceivingService {
    String messageKey();

    /**
     * Gets called if an redis message with the specified message key is received.
     * Receiving data only as string with key-value mapping.
     *
     * @param data Should be a key value or something else for calling an update trigger
     */
    void onReceive(final String data);
}
