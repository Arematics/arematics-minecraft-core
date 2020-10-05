package com.arematics.minecraft.core.pages;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class Page {
    private final List<String> content;
}
