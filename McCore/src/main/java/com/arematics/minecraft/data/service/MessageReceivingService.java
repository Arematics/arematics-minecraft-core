package com.arematics.minecraft.data.service;

public interface MessageReceivingService {
    String messageKey();
    void onReceive(final String data);
}
