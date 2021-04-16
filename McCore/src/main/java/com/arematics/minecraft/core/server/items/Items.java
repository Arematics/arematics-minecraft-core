package com.arematics.minecraft.core.server.items;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import lombok.RequiredArgsConstructor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class Items {

    public static final CoreItem PLAYERHOLDER;
    public static final CoreItem NEXT_PAGE;
    public static final CoreItem BEFORE_PAGE;
    public static final CoreItem BACK;
    public static final List<CoreItem> NUMBERS = new ArrayList<CoreItem>(){{
        add(getSkullWithDisplay("§c0", "http://textures.minecraft.net/texture/3f09018f46f349e553446946a38649fcfcf9fdfd62916aec33ebca96bb21b5"));
        add(getSkullWithDisplay("§c1", "http://textures.minecraft.net/texture/ca516fbae16058f251aef9a68d3078549f48f6d5b683f19cf5a1745217d72cc"));
        add(getSkullWithDisplay("§c2", "http://textures.minecraft.net/texture/4698add39cf9e4ea92d42fadefdec3be8a7dafa11fb359de752e9f54aecedc9a"));
        add(getSkullWithDisplay("§c3", "http://textures.minecraft.net/texture/fd9e4cd5e1b9f3c8d6ca5a1bf45d86edd1d51e535dbf855fe9d2f5d4cffcd2"));
        add(getSkullWithDisplay("§c4", "http://textures.minecraft.net/texture/f2a3d53898141c58d5acbcfc87469a87d48c5c1fc82fb4e72f7015a3648058"));
        add(getSkullWithDisplay("§c5", "http://textures.minecraft.net/texture/d1fe36c4104247c87ebfd358ae6ca7809b61affd6245fa984069275d1cba763"));
        add(getSkullWithDisplay("§c6", "http://textures.minecraft.net/texture/3ab4da2358b7b0e8980d03bdb64399efb4418763aaf89afb0434535637f0a1"));
        add(getSkullWithDisplay("§c7", "http://textures.minecraft.net/texture/297712ba32496c9e82b20cc7d16e168b035b6f89f3df014324e4d7c365db3fb"));
        add(getSkullWithDisplay("§c8", "http://textures.minecraft.net/texture/abc0fda9fa1d9847a3b146454ad6737ad1be48bdaa94324426eca0918512d"));
        add(getSkullWithDisplay("§c9", "http://textures.minecraft.net/texture/d6abc61dcaefbd52d9689c0697c24c7ec4bc1afb56b8b3755e6154b24a5d8ba"));
        add(getSkullWithDisplay("§c.", "http://textures.minecraft.net/texture/6ff99ff279a2cf25deb4bd5b66c3576b824cc96c36781027af727ed3a4c1308e"));
    }};

    static{
        ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getData());
        PLAYERHOLDER = CoreItem.create(itemStack).disableClick().setName("§7");
        NEXT_PAGE = nextPage().bindCommand("page NEXT");
        BEFORE_PAGE = pageBefore().bindCommand("page BEFORE");
        BACK = CoreItem.generate(Material.NETHER_STAR).bindCommand("cmdback").setName("§fBack");
    }

    private final List<CoreItemCreationModifier> coreItemCreationModifiers;

    public CoreItem generate(Material material){
        return create(new ItemStack(material));
    }

    public CoreItem create(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return null;
        CoreItem source = new CoreItem(item);
        for(CoreItemCreationModifier modifier : coreItemCreationModifiers) source = modifier.modify(source);
        return source;
    }

    public CoreItem[] create(ItemStack[] items){
        return Arrays.stream(items).map(this::create).toArray(CoreItem[]::new);
    }

    public CoreItem generateNoModifier(Material material){
        return createNoModifier(new ItemStack(material));
    }

    public CoreItem createNoModifier(ItemStack item){
        if(item == null || item.getType() == Material.AIR) return null;
        return new CoreItem(item);
    }

    public void giveItemTo(CorePlayer player, ItemStack itemStack){
        giveItem(player, create(itemStack));
    }

    public void giveItemsTo(CorePlayer player, ItemStack... contents){
        Arrays.stream(contents)
                .filter(item -> item != null && item.getType() != Material.AIR)
                .forEach(item -> giveItemTo(player, item));
    }

    public void giveItemsTo(CorePlayer player, List<CoreItem> items){
        items.stream().filter(Objects::nonNull).forEach(item -> giveItemTo(player, item));
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

    /**
     *
     * @param player
     * @param item
     */
    @Deprecated
    public static void giveItem(CorePlayer player, ItemStack... item){
        AtomicInteger integer = new AtomicInteger();
        for(ItemStack ite : item){

            int slotFree = 0;

            for(int i = 0; i < player.getPlayer().getInventory().getSize(); i++){
                ItemStack it = player.getPlayer().getInventory().getItem(i);
                if(it == null || ite.getType() == Material.AIR)
                    slotFree++;
            }

            if(slotFree == 0){
                ArematicsExecutor.syncRun(() -> {
                    Item drop = player.getPlayer().getWorld().dropItem(player.getPlayer().getLocation(), ite);
                    drop.setVelocity(new Vector(0, 0, 0));
                    integer.addAndGet(1);
                });
            }else ArematicsExecutor.syncRun(() -> player.getPlayer().getInventory().addItem(ite));

        }

        if(integer.get() > 0) player.warn("Inventory is full. Items have been dropped").handle();
    }
}
