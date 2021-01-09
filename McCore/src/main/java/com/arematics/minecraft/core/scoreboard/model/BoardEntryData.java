package com.arematics.minecraft.core.scoreboard.model;

import lombok.ToString;

@ToString
public class BoardEntryData {

    public String NAME;
    public String PREFIX;
    public String SUFFIX;

    public BoardEntryData(String name, String prefix, String suffix){
        this.NAME = name;
        this.PREFIX = prefix;
        this.SUFFIX = suffix;
    }
}
