package com.arematics.minecraft.core.server;

import lombok.Data;

@Data
public class GameServer {

    private final String globalName;
    private final String internName;
    private final String ip;
    private final int port;
}
