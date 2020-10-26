package com.arematics.minecraft.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Table(name = "user_configurations")
public class Configuration {

    @Column(name = "value")
    private String value;
}
