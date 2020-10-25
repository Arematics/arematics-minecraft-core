package com.arematics.minecraft.core.data.model.placeholder;

import com.arematics.minecraft.core.data.model.message.ChatClickAction;
import com.arematics.minecraft.core.data.model.message.ChatHoverAction;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Data
@Table(name = "theme_placeholder")
public class ThemePlaceholder {

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
}
