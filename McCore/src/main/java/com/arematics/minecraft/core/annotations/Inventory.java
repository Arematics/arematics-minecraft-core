package com.arematics.minecraft.core.annotations;

import org.bukkit.event.inventory.InventoryType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inventory {
    InventoryType type();
    String key();
    String title();
    int slots() default 54;
}
