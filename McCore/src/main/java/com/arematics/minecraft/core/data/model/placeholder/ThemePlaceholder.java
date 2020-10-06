package com.arematics.minecraft.core.data.model.placeholder;

import com.arematics.minecraft.core.chat.model.Placeholder;
import com.arematics.minecraft.core.data.model.message.ChatClickAction;
import com.arematics.minecraft.core.data.model.message.ChatHoverAction;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Data
@Table(name = "theme_placeholder")
public class ThemePlaceholder implements Placeholder {

    // without surrounding % %
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String placeholderKey;
    // with % %
    private String placeholderMatch;
    private String value;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ChatClickAction clickAction;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)

    private ChatHoverAction hoverAction;
    @Transient
    private boolean isStatic = true;
    @Transient
    private String belongingThemeKey;

    @Override
    public String getValue(Player player) {
        return value;
    }

    @Override
    public ChatClickAction getClickAction(String themeKey) {
        return getClickAction();
    }

    @Override
    public ChatHoverAction getHoverAction(String themeKey) {
        return getHoverAction();
    }

    @Override
    public void setClickAction(String themeKey, ChatClickAction clickAction) {
        setClickAction(clickAction);
    }


    @Override
    public void setHoverAction(String themeKey, ChatHoverAction hoverAction) {
        setHoverAction(hoverAction);
    }

}
