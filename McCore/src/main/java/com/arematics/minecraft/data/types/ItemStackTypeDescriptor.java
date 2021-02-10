package com.arematics.minecraft.data.types;

import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;

public class ItemStackTypeDescriptor extends AbstractTypeDescriptor<ItemStack[]> {

    public static final ItemStackTypeDescriptor INSTANCE =
            new ItemStackTypeDescriptor();

    public ItemStackTypeDescriptor() {
        super(ItemStack[].class, new ImmutableMutabilityPlan<>());
    }

    @Override
    public ItemStack[] fromString(String string) {
        return new ItemStack[0];
    }

    @SneakyThrows
    @Override
    public <X> X unwrap(ItemStack[] value, Class<X> type, WrapperOptions options) {

        if (value == null)
            return null;

        if (Blob.class.isAssignableFrom(type)) {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            BukkitObjectOutputStream outputStream = new BukkitObjectOutputStream(bytesOut);
            outputStream.writeObject(value);
            outputStream.flush();
            outputStream.close();
            return (X) options.getLobCreator().createBlob(bytesOut.toByteArray());
        }

        throw unknownUnwrap(type);
    }

    @SneakyThrows
    @Override
    public <X> ItemStack[] wrap(X value, WrapperOptions options) {
        if (value == null)
            return null;

        if(value instanceof Blob) {
            BukkitObjectInputStream in = new BukkitObjectInputStream(((Blob) value).getBinaryStream());
            return (ItemStack[]) in.readObject();
        }

        throw unknownWrap(value.getClass());
    }
}
