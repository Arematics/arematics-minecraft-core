package com.arematics.minecraft.core.server;

public class GameServer {

    private final String globalName;
    private final String internName;
    private final String ip;
    private final int port;

    public GameServer(String globalName, String internName, String ip, int port){
        this.globalName = globalName;
        this.internName = internName;
        this.ip = ip;
        this.port = port;
    }

    public String getGlobalName() {
        return globalName;
    }

    public String getInternName() {
        return internName;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
