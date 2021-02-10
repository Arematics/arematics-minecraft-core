package com.arematics.minecraft.core.items;

import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Material;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
public class CoreItem extends ItemStack {

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

    public static CoreItem generate(Material material){
        return CoreItem.create(new ItemStack(material));
    }

    public static CoreItem[] create(ItemStack[] items){
        return Arrays.stream(items)
                .map(CoreItem::create)
                .collect(Collectors.toList())
                .toArray(new CoreItem[]{});
    }

    public static void executeOnHandItem(CorePlayer player, Consumer<CoreItem> execute){
        CoreItem itemStack = CoreItem.create(player.getItemInHand());
        if(itemStack != null) execute.accept(itemStack);
        else player.warn("no_item_in_hand").handle();
    }

    private final NBTItem meta;

    private CoreItem(ItemStack item){
        super(item);
        this.meta = new NBTItem(this, true);
    }

    public NBTItem getMeta(){
        return meta;
    }

    public CoreItem bindCommand(String command){
        this.getMeta().setString(BINDED_COMMAND, command.replaceFirst("/", ""));
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

    public CoreItem verified(){
        this.getMeta().setString("verified", "42774");
        this.applyNBT();
        return this.addToLore("§aVerified Item §8(See more about verified items /faq)");
    }

    public String readMetaValue(String key){
        return this.getMeta().getString(key);
    }

    public boolean hasBindedCommand(){
        return this.getMeta().hasKey(BINDED_COMMAND);
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

    public CoreItem removeString(String key){
        this.getMeta().removeKey(key);
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

    public boolean containsLore(String sequence){
        List<String> lore = this.getLore();
        if(lore != null)
            return lore.stream().anyMatch(row -> row.contains(sequence));
        return false;
    }

    public int findFirst(String sequence){
        List<String> lore = this.getLore();
        return IntStream.range(0, lore.size())
                .filter(index -> lore.get(index).contains(sequence))
                .findFirst()
                .orElse(-1);
    }
    public <E extends Enum<E>> CoreItem bindEnumLore(E value){
        List<E> enums = EnumUtils.getEnumList(value.getDeclaringClass());
        this.clearLore();
        enums.forEach(item -> addToLore((item.equals(value) ? "§a" : "§8") + "> " + item.name().replaceAll("_", " ")));
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

    public CoreItem executeAndRegister(Server server, CorePlayer player, Function<CoreItem, CoreItem> action){
        CoreItem clone = action.apply(this);
        ArematicsExecutor.syncDelayed(() -> server.registerItemListener(player, clone, action), 250, TimeUnit.MILLISECONDS);
        return clone;
    }

    public CoreItem register(Server server, CorePlayer player, Function<CoreItem, CoreItem> action){
        ArematicsExecutor.syncDelayed(() -> server.registerItemListener(player, this, action), 250, TimeUnit.MILLISECONDS);
        return this;
    }

    public boolean isArmor(){
        return this.getMeta() != null;
    }

    public void updateTo(Player player){
        player.setItemInHand(this);
    }

    public void updateTo(CorePlayer player){
        player.getPlayer().setItemInHand(this);
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
        return "CoreItem{item" + super.toString() + "}'";
    }

    @SneakyThrows
    @Override
    public Map<String, Object> serialize() {
        return super.serialize();
    }

    public static CoreItem deserialize(Map<String, Object> args){
        return new CoreItem(ItemStack.deserialize(args));
    }


}
