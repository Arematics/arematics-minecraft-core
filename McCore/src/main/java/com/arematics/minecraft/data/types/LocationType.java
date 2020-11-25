package com.arematics.minecraft.data.types;

import org.bukkit.Location;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;
import org.springframework.stereotype.Component;

public class LocationType extends AbstractSingleColumnStandardBasicType<Location> {

    public static final LocationType INSTANCE = new LocationType();

    public LocationType() {
        super(VarcharTypeDescriptor.INSTANCE, LocationTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "Location";
    }
}
