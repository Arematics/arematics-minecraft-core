package com.arematics.minecraft.data.types;

import com.arematics.minecraft.core.utils.LocationParser;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.springframework.stereotype.Component;

public class LocationTypeDescriptor extends AbstractTypeDescriptor<Location> {

    public static final LocationTypeDescriptor INSTANCE =
            new LocationTypeDescriptor();

    public LocationTypeDescriptor() {
        super(Location.class, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public Location fromString(String string) {
        return LocationParser.toLocation(string);
    }

    @SneakyThrows
    @Override
    public <X> X unwrap(Location value, Class<X> type, WrapperOptions options) {

        if (value == null)
            return null;

        if (String.class.isAssignableFrom(type))
            return (X) LocationParser.fromLocation(value);
        throw unknownUnwrap(type);
    }

    @SneakyThrows
    @Override
    public <X> Location wrap(X value, WrapperOptions options) {
        if (value == null)
            return null;

        if(value instanceof String)
            return LocationParser.toLocation((String) value);

        throw unknownWrap(value.getClass());
    }
}