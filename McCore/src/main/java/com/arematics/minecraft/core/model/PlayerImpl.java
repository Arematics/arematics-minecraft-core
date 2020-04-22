package com.arematics.minecraft.core.model;

import com.arematics.model.Player;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PlayerImpl implements Player {
    private String arematicsId;
    private String uuid;
    private String name;
    private Long totalTime;
    private Long afkTime;
    private int friends;
}
