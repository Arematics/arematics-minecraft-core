package com.arematics.minecraft.data.model.message;

import com.arematics.minecraft.core.messaging.advanced.ClickAction;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Data
@NoArgsConstructor
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
