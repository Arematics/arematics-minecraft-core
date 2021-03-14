package com.arematics.minecraft.core.server.entities.player.inventories.helper;

import com.arematics.minecraft.core.items.CoreItem;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class Range implements IntegerBox {

    public static Range allOthersInRow(Inventory inv, int row, CoreItem... items){
        List<CoreItem> clones = Arrays.asList(items);
        ItemStack[] contents = inv.getContents();
        return new Range(IntStream.range(row * 9, row * 9 + 8)
                .filter(index -> noMatchingItem(contents[index], clones))
                .toArray());
    }

    public static Range custom(int... integers){
        return new Range(integers);
    }

    public static Range inRow(int row, int... indexes){
        row--;
        int startIndex = row * 9;
        return new Range(Arrays.stream(indexes).map(index -> startIndex + index).toArray());
    }

    public static Range allInRow(Inventory inv, int row, CoreItem... items){
        List<CoreItem> clones = Arrays.asList(items);
        ItemStack[] contents = inv.getContents();
        return new Range(IntStream.range(row * 9, row * 9 + 8)
                .filter(index -> !noMatchingItem(contents[index], clones))
                .toArray());
    }

    public static Range allHardInRow(int rowStartIndex, int rowEndIndex, int row){
        return new Range(IntStream.range(row * 9 + rowStartIndex, row * 9 + rowEndIndex + 1)
                .toArray());
    }

    public static Range allEmptiesInRows(Inventory inv, int... rows){
        return fromRanges(IntStream.of(rows).boxed()
                .map(index -> allInRow(inv, index))
                .toArray(Range[]::new));
    }

    public static Range allHardInRows(int rowStartIndex, int rowEndIndex, int... rows){
        return fromRanges(IntStream.of(rows).boxed()
                .map(index -> allHardInRow(rowStartIndex, rowEndIndex, index))
                .toArray(Range[]::new));
    }

    public static Range fromRanges(Range... ranges){
        return new Range(Arrays.stream(ranges).flatMap(range -> range.toList().stream()).mapToInt(range -> range).toArray());
    }

    private static boolean noMatchingItem(ItemStack inventoryItem, List<CoreItem> clones){
        if(inventoryItem == null) return true;
        return clones.stream().noneMatch(item -> item.isSimilar(inventoryItem));
    }

    private final int[] indexes;

    @Override
    public int[] getIndexes() {
        return indexes;
    }

    @Override
    public List<Integer> toList(){
        return IntStream.of(indexes).boxed().collect(Collectors.toList());
    }
}
