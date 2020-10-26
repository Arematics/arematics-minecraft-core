package com.arematics.minecraft.data.global.model;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "theme_mapping")
    private Set<ThemePlaceholder> themePlaceholders = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<GlobalPlaceholderActions> globalPlaceholderActions = new ArrayList<>();

    @Transient
    private List<ChatThemeUser> activeUsers = new ArrayList<>();

}
