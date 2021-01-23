package com.arematics.minecraft.data.global.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "global_placeholder_action")
public class GlobalPlaceholderAction implements PlaceholderAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String placeholderKey;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ChatHoverAction hoverAction;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ChatClickAction clickAction;
}
