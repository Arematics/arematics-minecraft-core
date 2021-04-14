package com.arematics.minecraft.data.mode.model;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.data.global.model.BukkitItemMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_price")
public class ItemPrice implements BukkitItemMapper, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private double price;

    public Integer getItemId(){
        return Integer.parseInt(id.split(":")[0]);
    }

    @Override
    public CoreItem mapToItem(Server server) {
        String[] split = id.split(":");
        ItemStack item = new ItemStack(Integer.parseInt(split[0]));
        if(split.length == 2) item.setDurability(Short.parseShort(split[1]));
        return server.items().createNoModifier(item)
                .disableClick()
                .addToLore(" ", "§8Per Item Sell Price: §e" + price + " Coins");
    }
}
