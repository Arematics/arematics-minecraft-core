package com.arematics.minecraft.data.types;

import org.bukkit.inventory.ItemStack;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;

public class ItemStackType extends AbstractSingleColumnStandardBasicType<ItemStack[]> {

    public static final ItemStackType INSTANCE = new ItemStackType();

    public ItemStackType() {
        super(BlobTypeDescriptor.BLOB_BINDING, ItemStackTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "ItemStack";
    }
}
