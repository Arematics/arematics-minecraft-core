package com.arematics.minecraft.core.exceptions;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.Getter;

@Getter
public class CurrencyTransferException extends RuntimeException{

    private final CorePlayer executor;

    public CurrencyTransferException(CorePlayer executor, String message){
        super(message);
        this.executor = executor;
    }
}
