package com.arematics.minecraft.core.data.model.placeholder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "global_placeholder")
public class GlobalPlaceholder {

    // without surrounding % %
    @Id
    private String placeholderKey;
    // with % %
    private String placeholderMatch;

    @Transient
    private boolean isStatic = false;


}
