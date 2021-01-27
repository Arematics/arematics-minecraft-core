package com.arematics.minecraft.core.items;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

@Component
public class DisableClickItemExecutor extends ItemClickExecutor {

    public DisableClickItemExecutor(){
        super("disable_click");
    }

    @Override
    public boolean execute(CorePlayer clicked, CoreItem item) {
        return true;
    }
}
