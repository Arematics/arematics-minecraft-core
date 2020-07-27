package com.arematics.minecraft.core.permissions;

import org.bukkit.command.CommandSender;

public class Permissions {

    public static boolean isNotAllowed(CommandSender sender, String permission){
        return !(hasUpperPermissionLevel(sender, permission) || hasLowerPermissionLevel(sender, permission));
    }

    /**
     * Check if Player has Class Permission Level
     * @param sender CommandSender
     * @param permission Permission Name
     * @return Player has Class Permission Level
     */
    private static boolean hasUpperPermissionLevel(CommandSender sender, String permission){
        if(!permission.contains(".")) return permission.equalsIgnoreCase("sound");
        String[] upper = permission.split("\\.");
        return upper[0].equalsIgnoreCase("sound");
    }

    /**
     * Check if Player has direct Permission Level
     * @param sender CommandSender
     * @param permission Permission Name
     * @return Player has direct Permission Level
     */
    private static boolean hasLowerPermissionLevel(CommandSender sender, String permission){
        return true;
    }
}
