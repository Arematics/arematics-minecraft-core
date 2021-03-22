package com.arematics.minecraft.core.server.items;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Represents some special mob heads, also support creating player skulls and custom skulls.
 *
 * @author xigsag, SBPrime @change Klysma
 */
public class Skull {

	private static Class<?> skullMetaClass;
	
	static {
		 String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		 try {
			 skullMetaClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftMetaSkull");
		 } catch (ClassNotFoundException e) {
			 e.printStackTrace();
		 }
	}
    private final String id;

    private Skull(String id) {
        this.id = id;
    }

    
    public static ItemStack getCustomSkull(String url, String displayname) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        String base64encoded = Base64.getEncoder().encodeToString((new String("{textures:{SKIN:{url:\"" + url + "\"}}}").getBytes()));
        Property property = new Property("textures", base64encoded);
        profile.getProperties().put("textures", property);
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(displayname);
        try {
        	Field profileField = skullMetaClass.getDeclaredField("profile");
        	profileField.setAccessible(true);
        	profileField.set(meta, profile);
        } catch (Exception ex) {
        ex.printStackTrace();
        }
        head.setItemMeta(meta);
        return head;
    }
    
    public static ItemStack getCustomSkull(String url, String displayname, List<String> lore) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        String base64encoded = Base64.getEncoder().encodeToString((new String("{textures:{SKIN:{url:\"" + url + "\"}}}").getBytes()));
        Property property = new Property("textures", base64encoded);
        profile.getProperties().put("textures", property);
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(displayname);
        meta.setLore(lore);
        try {
        	Field profileField = skullMetaClass.getDeclaredField("profile");
        	profileField.setAccessible(true);
        	profileField.set(meta, profile);
        } catch (Exception ex) {
        ex.printStackTrace();
        }
        head.setItemMeta(meta);
        return head;
    }
    
    public static ItemStack getCustomSkull(String url, List<String> lore) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        String base64encoded = Base64.getEncoder().encodeToString((new String("{textures:{SKIN:{url:\"" + url + "\"}}}").getBytes()));
        Property property = new Property("textures", base64encoded);
        profile.getProperties().put("textures", property);
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setLore(lore);
        try {
        	Field profileField = skullMetaClass.getDeclaredField("profile");
        	profileField.setAccessible(true);
        	profileField.set(meta, profile);
        } catch (Exception ex) {
        ex.printStackTrace();
        }
        head.setItemMeta(meta);
        return head;
    }

    
    public static ItemStack getPlayerSkull(String name) {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwner(name);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    
    public static ItemStack getPlayerSkull(String name, String display) {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwner(name);
        meta.setDisplayName(display);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    
    public String getId() {
        return id;
    }

    
    public ItemStack getSkull() {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwner(id);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

}
