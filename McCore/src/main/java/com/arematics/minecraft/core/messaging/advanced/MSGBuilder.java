package com.arematics.minecraft.core.messaging.advanced;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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

    public static MSG joinMessages(List<MSG> msgs, char joining){
        return buildMessages(msgs, joining);
    }

    private static MSG build(List<Part> values, char joining){
        if(values == null) return new MSG("");
        if(values.size() == 1) return new MSG(values);
        else return joinParts(new ArrayList<>(values), joining);
    }

    private static MSG buildMessages(List<MSG> values, char joining){
        if(values == null) return new MSG("");
        if(values.size() == 1) return values.get(0);
        else return joinMessages(new ArrayList<>(values), joining);
    }

    private static MSG joinParts(ArrayList<Part> parts, char joining){
        IntStream.range(1, parts.size())
                .forEach(index -> parts.add(parts.lastIndexOf(new Part(joining + " ")) + 2, new Part(joining + " ")));
        return new MSG(parts);
    }

    private static MSG joinMessages(ArrayList<MSG> parts, char joining){
        IntStream.range(1, parts.size())
                .forEach(index -> parts.get(index).PARTS.add(0, new Part(joining + " ")));
        return new MSG(parts.stream().flatMap(msg -> msg.PARTS.stream()).collect(Collectors.toList()));
    }
}
