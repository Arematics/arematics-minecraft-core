package com.arematics.minecraft.core.server.entities.player.inventories.helper;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class Box implements IntegerBox {

    public static Box create(){
        return new Box();
    }

    private final List<Range> rangeList = new ArrayList<>();

    public Box addRange(Range range){
        this.rangeList.add(range);
        return this;
    }

    public Box removeRange(Range range){
        this.rangeList.remove(range);
        return this;
    }

    @Override
    public int[] getIndexes() {
        return rangeList.stream().flatMap(range -> range.toList().stream()).mapToInt(range -> range).toArray();
    }

    @Override
    public List<Integer> toList() {
        return rangeList.stream().flatMap(range -> range.toList().stream()).collect(Collectors.toList());
    }
}
