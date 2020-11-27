package com.arematics.minecraft.data.types;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.bukkit.Color;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class ColorTypeDescriptor extends AbstractTypeDescriptor<Color> {

    public static final ColorTypeDescriptor INSTANCE =
            new ColorTypeDescriptor();

    public ColorTypeDescriptor() {
        super(Color.class, new ImmutableMutabilityPlan<>());
    }

    @Override
    public Color fromString(String string) {
        return Color.fromRGB(0, 0, 0);
    }

    @SneakyThrows
    @Override
    public <X> X unwrap(Color value, Class<X> type, WrapperOptions options) {

        if (value == null)
            return null;

        Gson gson = new Gson();
        if (String.class.isAssignableFrom(type))
            return (X) gson.toJson(value);

        throw unknownUnwrap(type);
    }

    @SneakyThrows
    @Override
    public <X> Color wrap(X value, WrapperOptions options) {
        if (value == null)
            return null;

        Gson gson = new Gson();
        if(value instanceof String)
            return gson.fromJson((String)value, Color.class);

        throw unknownWrap(value.getClass());
    }
}
