package com.arematics.minecraft.core.permissions;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.service.UserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Permissions {

    private static final Map<String, Boolean> permissionCache = new HashMap<>();

    /**
     * Check if sender has Permission over Rank Permissions and User Permissions.
     * Returns true if sender is not a player
     * @param sender Command Executor
     * @param permission Permission searching for
     * @return Command Executor has permission access
     */
    public static boolean hasPermission(CommandSender sender, String permission){
        String key = keyFromData(sender.getName(), permission);
        if(permissionCache.containsKey(key)) return permissionCache.get(key);
        if(sender instanceof Player)
            return hasPermission(((Player)sender).getUniqueId(), permission);
        return true;
    }
    /**
     * Check if UUID has Permission over Rank Permissions and User Permissions.
     * @param uuid Universally Unique Identifier searching permission for
     * @param permission Permission searching for
     * @return UUID has permission access
     */
    public static boolean hasPermission(UUID uuid, String permission){
        String key = keyFromData(uuid.toString(), permission);
        if(permissionCache.containsKey(key)) return permissionCache.get(key);
        UserService service = Boots.getBoot(CoreBoot.class).getContext().getBean(UserService.class);
        return service.hasPermission(uuid, permission);
    }

    /**
     * Permission Check Consumer executing method if permission is given or other method if permission is not given.
     * @param sender Command Executor
     * @param permission Permission searching for
     * @return Class with ifPermitted Consumer and orElse Consumer
     */
    public static PermConsumer check(CorePlayer sender, String permission){
        return new PermissionData(sender, permission);
    }

    private static String keyFromData(String key, String permission){
        return key + "." + permission;
    }
}
