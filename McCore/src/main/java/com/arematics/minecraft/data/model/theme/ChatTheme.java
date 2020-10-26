package com.arematics.minecraft.data.model.theme;

import com.arematics.minecraft.data.model.placeholder.GlobalPlaceholderActions;
import com.arematics.minecraft.data.model.placeholder.ThemePlaceholder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

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
