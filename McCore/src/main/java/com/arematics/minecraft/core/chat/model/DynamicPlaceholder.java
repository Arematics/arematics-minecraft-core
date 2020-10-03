package com.arematics.minecraft.core.chat.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class DynamicPlaceholder implements Placeholder {

    // without surrounding % %
    private String placeholderName;
    // with % %
    private String placeholderMatch;

    private Map<Player, String> placeholderValues = new HashMap<>();

}
