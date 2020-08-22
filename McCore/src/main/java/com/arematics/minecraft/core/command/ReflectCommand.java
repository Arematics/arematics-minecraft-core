package com.arematics.minecraft.core.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReflectCommand extends Command {
    private CoreCommand exe = null;

    protected ReflectCommand(String command) { super(command); }

    public void setExecutor(CoreCommand exe) { this.exe = exe; }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (exe != null) { return exe.onCommand(sender, this, commandLabel, args); }
        return false;
    }
}
