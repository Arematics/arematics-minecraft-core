package com.arematics.minecraft.core.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Permissions {

    public static boolean isNotAllowed(CommandSender sender, String permission){
        if(sender instanceof Player) {
            return !(hasUpperPermissionLevel((Player)sender, permission) ||
                    hasLowerPermissionLevel((Player)sender, permission));
        }
        return true;
    }

    /**
     * Check if Player has Class Permission Level
     * @param player Player
     * @param permission Permission Name
     * @return Player has Class Permission Level
     */
    private static boolean hasUpperPermissionLevel(Player player, String permission){
        if(!permission.contains(".")) return permission.equalsIgnoreCase("sound");
        String[] upper = permission.split("\\.");
        return upper[0].equalsIgnoreCase("sound");
    }

    /**
     * Check if Player has direct Permission Level
     * @param player Player
     * @param permission Permission Name
     * @return Player has direct Permission Level
     */
    private static boolean hasLowerPermissionLevel(Player player, String permission){
        return true;
    }
}
