package com.arematics.minecraft.core.data.model.theme;

import com.arematics.minecraft.core.chat.model.GlobalPlaceholderActions;
import com.arematics.minecraft.core.data.model.message.ChatClickAction;
import com.arematics.minecraft.core.data.model.message.ChatHoverAction;
import com.arematics.minecraft.core.data.model.placeholder.ThemePlaceholder;
import lombok.*;
import org.bukkit.entity.Player;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.*;
import java.util.function.Supplier;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "theme")
public class ChatTheme {

    @Id
    private String themeKey;
    private String format;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "theme_mapping")
    private Set<ThemePlaceholder> themePlaceholders = new HashSet<>();
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<GlobalPlaceholderActions> dynamicWrapper = new ArrayList<>();

    @Transient
    // theme key
    private Map<String, Map<Player, Supplier<String>>> placeholderThemeValues = new HashMap<>();


    @Transient
    private List<ChatThemeUser> activeUsers = new ArrayList<>();

}
