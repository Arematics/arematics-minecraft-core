package com.arematics.minecraft.data.model.placeholder;

import com.arematics.minecraft.data.model.message.ChatClickAction;
import com.arematics.minecraft.data.model.message.ChatHoverAction;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;


@Data
@NoArgsConstructor
@Entity
public class GlobalPlaceholderActions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String placeholderKey;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ChatHoverAction hoverAction;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ChatClickAction clickAction;
}
