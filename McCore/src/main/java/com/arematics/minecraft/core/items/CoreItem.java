package com.arematics.minecraft.core.items;

import com.arematics.minecraft.core.messaging.Messages;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
public class CoreItem extends ItemStack implements ConfigurationSerializable {

    public static final String BINDED_COMMAND = "binded_command";
    public static final String DISABLE_CLICK = "disable_click";
    public static final String CLOSE_INVENTORY = "close_inventory_on_click";

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

    public static CoreItem[] create(ItemStack[] items){
        return Arrays.stream(items)
                .map(CoreItem::create)
                .collect(Collectors.toList())
                .toArray(new CoreItem[]{});
    }

    public static void executeOnHandItem(Player player, Consumer<CoreItem> execute){
        CoreItem itemStack = CoreItem.create(player.getItemInHand());
        if(itemStack != null) execute.accept(itemStack);
        else Messages.create("no_item_in_hand").WARNING().to(player).handle();
    }

    private final NBTItem meta;

    private CoreItem(ItemStack item){
        super(item);
        this.meta = new NBTItem(this, true);
    }

    public NBTItem getMeta(){
        return this.meta;
    }

    public CoreItem bindCommand(String command){
        this.getMeta().setString(BINDED_COMMAND, command);
        this.applyNBT();
        return this;
    }

    public CoreItem unbindCommand(){
        this.getMeta().removeKey(BINDED_COMMAND);
        this.applyNBT();
        return this;
    }

    public CoreItem disableClick(){
        this.getMeta().setString(DISABLE_CLICK, "true");
        this.applyNBT();
        return this;
    }

    public CoreItem closeInventoryOnClick(){
        this.getMeta().setString(CLOSE_INVENTORY, "true");
        this.applyNBT();
        return this;
    }

    public boolean closeOnClick(){
        return this.getMeta().hasKey(CLOSE_INVENTORY);
    }

    public boolean clickDisabled(){
        return this.getMeta().hasKey(DISABLE_CLICK);
    }

    public String readMetaValue(String key){
        return this.getMeta().getString(key);
    }

    public boolean hasBindedCommand(){
        return this.getMeta().hasKey(BINDED_COMMAND);
    }

    public String getBindedCommand(){
        return this.getMeta().getString(BINDED_COMMAND);
    }

    public CoreItem setInteger(String key, int value){
        this.getMeta().setInteger(key, value);
        this.applyNBT();
        return this;
    }

    public CoreItem setString(String key, String value){
        this.getMeta().setString(key, value);
        this.applyNBT();
        return this;
    }

    public void applyNBT(){
        this.getMeta().applyNBT(this);
    }

    public CoreItem setName(String name){
        name = name.replaceAll("&", "§");
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

    public CoreItem clearName(){
        ItemMeta meta = this.getItemMeta();
        meta.setDisplayName(null);
        this.setItemMeta(meta);
        return this;
    }

    public CoreItem setGlow(){
        this.addUnsafeEnchantment(Enchantment.LUCK, 3);
        ItemMeta meta = this.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        this.setItemMeta(meta);
        return this;
    }

    public boolean isArmor(){
        return this.getMeta() != null;
    }

    public

    public void updateTo(Player player){
        player.setItemInHand(this);
    }

    public BukkitObjectOutputStream toSingleStream() throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(bytesOut);
        outputStream.writeObject(this);
        outputStream.flush();
        outputStream.close();
        return outputStream;
    }

    @Override
    public String toString() {
        return "CoreItem{item" + super.toString() +
                ",meta=" + meta +
                '}';
    }

    @Override
    public Map<String, Object> serialize() {
        return super.serialize();
    }

    public static CoreItem deserialize(Map<String, Object> args){
        return new CoreItem(ItemStack.deserialize(args));
    }


}
