package com.arematics.minecraft.core.permissions;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.data.global.model.Permission;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.service.UserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Predicate;

public class Permissions {

    public static boolean isNotAllowed(CommandSender sender, String permission){
        if(sender instanceof Player)
            return !hasPermission(((Player)sender).getUniqueId(), permission);
        return true;
    }

    public static boolean hasPermission(CommandSender sender, String permission){
        if(sender instanceof Player)
            return hasPermission(((Player)sender).getUniqueId(), permission);
        return true;
    }

    public static boolean hasPermission(UUID uuid, String permission){
        UserService service = Boots.getBoot(CoreBoot.class).getContext().getBean(UserService.class);
        return service.hasPermission(uuid, permission);
    }

    /**
     * Please use @link #hasPermission(UUID, String) instead
     * @param user Database User
     * @param permission Permission
     * @return
     */
    @Deprecated
    public static boolean hasPermission(User user, String permission){
        UserService service = Boots.getBoot(CoreBoot.class).getContext().getBean(UserService.class);
        return service.hasPermission(user.getUuid(), permission);
    }

    public static PermConsumer check(CommandSender sender, String permission){
        return new PermissionData(sender, permission);
    }

    private static Predicate<? super Permission> hasPerm(String permission){
        return perm -> perm.getPermission().equals(permission) ||
                perm.getPermission().equals(permission.split("\\.")[0]);
    }
}
