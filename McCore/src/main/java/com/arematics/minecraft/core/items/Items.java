package com.arematics.minecraft.core.items;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class Items {

    public static final CoreItem PLAYERHOLDER;
    public static final CoreItem NEXT_PAGE;
    public static final CoreItem BEFORE_PAGE;

    static{
        ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.GRAY.getData());
        PLAYERHOLDER = CoreItem.create(itemStack).disableClick().setName("§7");
        NEXT_PAGE = nextPage().bindCommand("page NEXT");
        BEFORE_PAGE = pageBefore().bindCommand("page BEFORE");
    }

    private static CoreItem pageBefore(){
        return getSkullWithDisplay("§c<- Page before",
                "http://textures.minecraft.net/texture/bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9");
    }

    private static CoreItem nextPage(){
        return getSkullWithDisplay("§cNext Page ->",
                "http://textures.minecraft.net/texture/19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf");
    }

    public static CoreItem fetchPlayerSkull(String name){
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(name);
        skull.setItemMeta(meta);
        return CoreItem.create(skull);
    }

    public static CoreItem getSkullWithDisplay(String Display, String path){
        return CoreItem.create(Skull.getCustomSkull(path, Display));
    }

    public static CoreItem getSkullWithDisplayAndLore(String Display, String path, List<String> lore){
        return CoreItem.create(Skull.getCustomSkull(path, Display, lore));
    }

    public static CoreItem getSkullWithLore(String path, List<String> lore){
        return CoreItem.create(Skull.getCustomSkull(path, lore));
    }
}
