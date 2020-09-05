package com.arematics.minecraft.core.data.types;

import com.arematics.minecraft.core.items.CoreItem;
import lombok.SneakyThrows;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

import java.sql.Blob;

public class CoreItemTypeDescriptor extends AbstractTypeDescriptor<CoreItem[]> {

    public static final CoreItemTypeDescriptor INSTANCE =
            new CoreItemTypeDescriptor();

    public CoreItemTypeDescriptor() {
        super(CoreItem[].class, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public CoreItem[] fromString(String string) {
        return new CoreItem[0];
    }

    @SneakyThrows
    @Override
    public <X> X unwrap(CoreItem[] value, Class<X> type, WrapperOptions options) {

        if (value == null)
            return null;

        if (Blob.class.isAssignableFrom(type))
            return (X) options.getLobCreator().createBlob(CoreItem.toStream(value).toByteArray());

        throw unknownUnwrap(type);
    }

    @SneakyThrows
    @Override
    public <X> CoreItem[] wrap(X value, WrapperOptions options) {
        if (value == null)
            return null;

        if(value instanceof Blob)
            return CoreItem.streamTo(((Blob) value).getBinaryStream());

        throw unknownWrap(value.getClass());
    }
}
