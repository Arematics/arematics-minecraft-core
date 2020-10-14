package com.arematics.minecraft.core.items;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class CoreItem extends ItemStack implements ConfigurationSerializable {

    public static CoreItem[] streamTo( InputStream inputStream) throws Exception {
        BukkitObjectInputStream in = new BukkitObjectInputStream(inputStream);
        return (CoreItem[]) in.readObject();
    }

    public static ByteArrayOutputStream toStream(CoreItem[] array) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(bytesOut);
        outputStream.writeObject(array);
        outputStream.flush();
        outputStream.close();
        return bytesOut;
    }

    public static CoreItem create(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return null;
        return new CoreItem(item);
    }

    private final NBTItem meta;

    private CoreItem(ItemStack item){
        super(item);
        this.meta = new NBTItem(this, true);
    }

    public NBTItem getMeta(){
        return this.meta;
    }

    public void setInteger(String key, int value){
        this.getMeta().setInteger(key, value);
    }

    public void applyNBT(){
        this.getMeta().applyNBT(this);
    }

    public BukkitObjectOutputStream toSingleStream() throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(bytesOut);
        outputStream.writeObject(this);
        outputStream.flush();
        outputStream.close();
        return outputStream;
    }

    public CoreItem setName(String name){
        ItemMeta meta = this.getItemMeta();
        meta.setDisplayName(name);
        this.setItemMeta(meta);
        return this;
    }

    private List<String> getLore(){
        ItemMeta meta = this.getItemMeta();
        if(meta.getLore() == null || meta.getLore().isEmpty())
            return new ArrayList<>();
        return meta.getLore();
    }

    public CoreItem setLoreAt(int index, String message){
        message = message.replaceAll("&", "§");
        ItemMeta meta = this.getItemMeta();
        List<String> lore = getLore();
        lore.set(index, message);
        meta.setLore(lore);
        this.setItemMeta(meta);
        return this;
    }

    public CoreItem addToLore(String message){
        message = message.replaceAll("&", "§");
        ItemMeta meta = this.getItemMeta();
        List<String> lore = getLore();
        lore.add(message);
        meta.setLore(lore);
        this.setItemMeta(meta);
        return this;
    }

    public CoreItem removeFromLore(int index){
        ItemMeta meta = this.getItemMeta();
        List<String> lore = getLore();
        lore.remove(index);
        meta.setLore(lore);
        this.setItemMeta(meta);
        return this;
    }

    public CoreItem clearLore(){
        ItemMeta meta = this.getItemMeta();
        meta.setLore(new ArrayList<>());
        this.setItemMeta(meta);
        return this;
    }

    public void updateTo(Player player){
        player.setItemInHand(this);
    }

    @Override
    public Map<String, Object> serialize() {
        return super.serialize();
    }

    public static CoreItem deserialize(Map<String, Object> args){
        return new CoreItem(ItemStack.deserialize(args));
    }
}
