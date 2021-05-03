package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.entities.player.world.InteractHandler;
import org.springframework.stereotype.Component;

@Component
public class CommandBackCommand extends CoreCommand {

    public CommandBackCommand(){
        super("cmdback");
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        sender.handle(InteractHandler.class).dispatchCommand(sender.getLastCommand(3));
    }
}
