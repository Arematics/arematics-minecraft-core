package com.arematics.minecraft.core.data.model.message;

import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
public class ChatClickAction {

    public ChatClickAction(ClickAction action, String value) {
        setAction(action);
        setValue(value);
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ClickAction action;
    private String value;
}
