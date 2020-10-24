package com.arematics.minecraft.core.data.model.placeholder;

import com.arematics.minecraft.core.chat.model.Placeholder;
import com.arematics.minecraft.core.data.model.message.ChatClickAction;
import com.arematics.minecraft.core.data.model.message.ChatHoverAction;
import com.arematics.minecraft.core.data.model.theme.ChatTheme;
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
public class DynamicPlaceholder {

    // without surrounding % %
    @Id
    private String placeholderKey;
    // with % %
    private String placeholderMatch;
    @Transient
    private boolean isStatic = false;

}
