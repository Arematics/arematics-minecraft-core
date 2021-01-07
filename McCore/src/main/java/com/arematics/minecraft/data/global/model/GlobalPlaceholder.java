package com.arematics.minecraft.data.global.model;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Data
@NoArgsConstructor
@Entity
@Table(name = "global_placeholder")
public class GlobalPlaceholder {

    @Id
    @Column(name = "placeholder_key")
    private String placeholderKey;
    @Formula(value = "concat('%', placeholder_key, '%')")
    private String placeholderMatch;

    @Transient
    private Map<CorePlayer, Supplier<String>> values = new HashMap<>();

    public String getValue(CorePlayer player) {
        return getValues().get(player).get();
    }

}
