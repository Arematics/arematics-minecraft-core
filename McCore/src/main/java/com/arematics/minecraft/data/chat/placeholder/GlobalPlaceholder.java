package com.arematics.minecraft.data.chat.placeholder;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.hibernate.annotations.Formula;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Data
@NoArgsConstructor
@Entity
@Table(name = "global_placeholder")
public class GlobalPlaceholder {

    @Id
    private String placeholderKey;
    @Formula(value = "concat('%', placeholder_key, '%')")
    private String placeholderMatch;

    @Transient
    private Map<Player, Supplier<String>> values = new HashMap<>();

}
