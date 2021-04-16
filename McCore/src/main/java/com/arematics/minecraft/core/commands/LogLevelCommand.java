package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.springframework.stereotype.Component;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@Component
@Perm(permission = "server.utils.loglevel", description = "Changes log level for console output only needed by devs")
public class LogLevelCommand extends CoreCommand {

    public LogLevelCommand(){
        super("loglevel", true, "debug");
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        if(Bukkit.getLogger().getLevel() == Level.CONFIG)
            Bukkit.getLogger().setLevel(Level.INFO);
        else
            Bukkit.getLogger().setLevel(Level.CONFIG);
        Bukkit.getLogger().addHandler(new LogHandler(Bukkit.getLogger().getLevel()));
        sender.info("Changed loglevel to " + Bukkit.getLogger().getLevel().toString()).handle();
    }

    @SubCommand("{level}")
    public void setLoggingLevelTo(CorePlayer sender, Level level) {
        Bukkit.getLogger().setLevel(level);
        Bukkit.getLogger().addHandler(new LogHandler(Bukkit.getLogger().getLevel()));
        sender.info("Changed loglevel to " + level.toString()).handle();
    }

    @RequiredArgsConstructor
    private class LogHandler extends Handler{

        private static final String DEFAULT_DEBUG_PREFIX_FORMAT = "[%1$s-%2$s] %3$s";
        private static final String DEFAULT_DEBUG_LOG_PREFIX = "DEBUG";

        private final Level level;

        @Override
        public void publish(LogRecord record) {
            if (Bukkit.getLogger().getLevel().intValue() <= record.getLevel().intValue()
                    && record.getLevel().intValue() < level.intValue()) {
                record.setLevel(Level.INFO);
                record.setMessage(String.format(DEFAULT_DEBUG_PREFIX_FORMAT,
                        "McCore",
                        DEFAULT_DEBUG_LOG_PREFIX,
                        record.getMessage().substring("McCore".length() + 3)));
            }
        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }
    }
}
