package com.arematics.minecraft.core.data.model.theme;

import com.arematics.minecraft.core.data.model.placeholder.GlobalPlaceholderActions;
import com.arematics.minecraft.core.data.model.placeholder.ThemePlaceholder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.util.*;
import java.util.function.Supplier;

@Data
@NoArgsConstructor
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
    private List<GlobalPlaceholderActions> globalPlaceholderActions = new ArrayList<>();

    @Transient
    private List<ChatThemeUser> activeUsers = new ArrayList<>();

}
