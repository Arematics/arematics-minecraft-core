package com.arematics.minecraft.core.data.model.placeholder;

import com.arematics.minecraft.core.chat.model.Placeholder;
import com.arematics.minecraft.core.data.model.message.ChatClickAction;
import com.arematics.minecraft.core.data.model.message.ChatHoverAction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "global_placeholder")
public class DynamicPlaceholder implements Placeholder {

    // without surrounding % %
    @Id
    private String placeholderKey;
    // with % %
    private String placeholderMatch;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)

    private Map<String, ChatClickAction> clickAction = new HashMap<>();
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Map<String, ChatHoverAction> hoverAction = new HashMap<>();
    @Transient
    private Map<Player, Supplier<String>> placeholderValues = new HashMap<>();
    @Transient
    private boolean isStatic = false;

    @Override
    public String getValue(Player player) {
        return getPlaceholderValues().get(player).get();
    }

    @Override
    public ChatClickAction getClickAction(String themeKey) {
        return getClickAction().get(themeKey);
    }

    @Override
    public ChatHoverAction getHoverAction(String themeKey) {
        return getHoverAction().get(themeKey);
    }

    @Override
    public void setClickAction(String themeKey, ChatClickAction clickAction) {
        getClickAction().put(themeKey, clickAction);
    }

    @Override
    public void setHoverAction(String themeKey, ChatHoverAction hoverAction) {
        getHoverAction().put(themeKey, hoverAction);
    }
}
