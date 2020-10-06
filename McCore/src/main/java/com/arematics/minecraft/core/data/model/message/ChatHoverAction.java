package com.arematics.minecraft.core.data.model.message;

import com.arematics.minecraft.core.messaging.advanced.HoverAction;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Data
@Entity
public class ChatHoverAction {

    public ChatHoverAction(HoverAction action, String value) {
        setAction(action);
        setValue(value);
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private HoverAction action;
    private String value;
}
