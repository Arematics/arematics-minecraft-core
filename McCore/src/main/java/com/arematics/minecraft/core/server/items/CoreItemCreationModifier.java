package com.arematics.minecraft.core.server.items;

import com.arematics.minecraft.core.items.CoreItem;

public interface CoreItemCreationModifier {
   CoreItem modify(CoreItem start);
}
