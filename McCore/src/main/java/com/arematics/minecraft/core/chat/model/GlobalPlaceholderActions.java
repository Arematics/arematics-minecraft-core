package com.arematics.minecraft.core.chat.model;

import com.arematics.minecraft.core.data.model.message.ChatClickAction;
import com.arematics.minecraft.core.data.model.message.ChatHoverAction;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class GlobalPlaceholderActions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String placeholderName;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ChatHoverAction hoverAction;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ChatClickAction clickAction;
}
