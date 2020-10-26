package com.arematics.minecraft.kits;

import com.arematics.minecraft.core.Bootstrap;
import com.arematics.minecraft.core.command.processor.parser.Parser;
import com.arematics.minecraft.kits.parser.KitParser;

public class KitBoot extends Bootstrap {

    public KitBoot() {
        super(false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Parser.getInstance().addParser(new KitParser());
    }

    @Override
    public void shutdown() {
    }
}
