package com.arematics.minecraft.data.types;

import com.arematics.minecraft.core.items.CoreItem;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;

public class CoreItemType extends AbstractSingleColumnStandardBasicType<CoreItem[]> {

    public static final CoreItemType INSTANCE = new CoreItemType();

    public CoreItemType() {
        super(BlobTypeDescriptor.BLOB_BINDING, CoreItemTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "CoreItem";
    }
}
