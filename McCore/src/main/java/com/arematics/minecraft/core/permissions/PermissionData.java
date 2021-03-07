package com.arematics.minecraft.core.permissions;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PermissionData implements PermConsumer {
    private final CorePlayer sender;
    private final String permission;
    private Consumer<CorePlayer> permitted = CorePlayer::getPlayer;

    @Override
    public PermissionData ifPermitted(Consumer<CorePlayer> permitted){
        this.permitted = permitted;
        return this;
    }

    public void orElse(Consumer<CorePlayer> orElse){
        if(StringUtils.isBlank(permission) || sender.hasPermission(permission))
            permitted.accept(sender);
        else orElse.accept(sender);
    }

    public void submit(){
        orElse(s -> s.warn("cmd_noperms").handle());
    }
}
