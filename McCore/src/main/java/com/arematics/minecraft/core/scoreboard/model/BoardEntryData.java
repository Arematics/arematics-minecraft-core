package com.arematics.minecraft.core.scoreboard.model;

public class BoardEntryData {

    public String NAME;
    public String PREFIX;
    public String SUFFIX;

    public BoardEntryData(String name, String prefix, String suffix){
        this.NAME = name;
        this.PREFIX = prefix;
        this.SUFFIX = suffix;
    }

    @Override
    public String toString() {
        return "BoardEntryData{" +
                "NAME='" + NAME + '\'' +
                ", PREFIX='" + PREFIX + '\'' +
                ", SUFFIX='" + SUFFIX + '\'' +
                '}';
    }
}
