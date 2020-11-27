package com.arematics.minecraft.data.types;

import org.bukkit.Color;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class ColorType extends AbstractSingleColumnStandardBasicType<Color> {

    public static final ColorType INSTANCE = new ColorType();

    public ColorType() {
        super(VarcharTypeDescriptor.INSTANCE, ColorTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "CrystalKey";
    }
}
