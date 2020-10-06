package com.arematics.minecraft.core.data.model.theme;

import com.arematics.minecraft.core.data.model.placeholder.DynamicPlaceholder;
import com.arematics.minecraft.core.data.model.placeholder.ThemePlaceholder;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "theme")
public class ChatTheme {

    @Id
    private String themeKey;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "global_mapping")
    private Map<String, DynamicPlaceholder> dynamicPlaceholders = new HashMap<>();
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "theme_mapping")
    private Set<ThemePlaceholder> themePlaceholders = new HashSet<>();
    private String format;
    @Transient
    private List<ChatThemeUser> activeUsers = new ArrayList<>();

}
