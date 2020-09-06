package com.arematics.minecraft.ui;

import com.arematics.minecraft.core.Bootstrap;

public class UIBoot extends Bootstrap {

    /**
     * Hooking Config File
     * Starts the Multi Hook to get all Hooks loaded (Language, Commands, Listener)
     */
    public UIBoot() throws Exception{
        super(true);
    }

    @Override
    public void shutdown() {

    }
}
