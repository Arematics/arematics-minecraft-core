package com.arematics.minecraft.data.global.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Table(name = "theme_placeholder")
public class ThemePlaceholder implements PlaceholderAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String placeholderKey;
    @Formula(value = "concat('%', placeholder_key, '%')")
    private String placeholderMatch;
    private String value;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ChatClickAction clickAction;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ChatHoverAction hoverAction;

}
