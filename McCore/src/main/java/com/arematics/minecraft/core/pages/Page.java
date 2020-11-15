package com.arematics.minecraft.core.pages;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;

import java.util.List;

@Data
@RequiredArgsConstructor
public class Page {
    private final List<String> content;
    private Inventory inventory;
}
