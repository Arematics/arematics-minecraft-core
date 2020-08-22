package com.arematics.minecraft.core.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Base64Utils {

    /**
     * ItemStack Array Base64 String with compression
     * @param contents ItemStack to convert
     * @return Compressed Base64 ItemStack Array String
     */
    public static String toBase64(ItemStack[] contents){
        try(ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                BukkitObjectOutputStream out = new BukkitObjectOutputStream(bytesOut)) {
            out.writeObject(contents);
            out.flush();
            out.close();
            return Base64Coder.encodeLines(bytesOut.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * ItemStack Array from compressed Base64 String
     * @param compressedBase64 Compressed Base64 String
     * @return ItemStack Array
     */
    public static ItemStack[] fromBase64(String compressedBase64){
        byte[] data = Base64Coder.decodeLines(compressedBase64);
        try(ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
                BukkitObjectInputStream in = new BukkitObjectInputStream(bytesIn)) {
            return (ItemStack[]) in.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
