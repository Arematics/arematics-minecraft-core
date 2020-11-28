package com.arematics.minecraft.core.messaging.advanced;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

public class MSGBuilder {

    public static MSG join(char joining, Part... values){
        return join(values, joining);
    }

    public static MSG join(Part[] values, char joining){
        return join(Arrays.asList(values), joining);
    }

    public static MSG join(List<Part> parts, char joining){
        return build(parts, joining);
    }

    private static MSG build(List<Part> values, char joining){
        if(values == null) return new MSG("");
        if(values.size() == 1) return new MSG(values);
        else return joinParts(new ArrayList<>(values), joining);
    }

    private static MSG joinParts(ArrayList<Part> parts, char joining){
        IntStream.range(1, parts.size())
                .forEach(index -> parts.add(parts.lastIndexOf(new Part(joining + " ")) + 2, new Part(joining + " ")));
        return new MSG(parts);
    }
}
