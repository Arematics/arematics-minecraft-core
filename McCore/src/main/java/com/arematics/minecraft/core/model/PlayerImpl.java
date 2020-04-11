package com.arematics.minecraft.core.model;

import com.arematics.model.Player;
import lombok.Data;

@Data
public class PlayerImpl implements Player {
    private String arematicsId;
    private String uuid;
    private String name;
    private Long totalTime;
    private Long afkTime;
    private int friends;

    @Override
    public String toString(){
        return "{arematicsId: '" + getArematicsId() + "', uuid: '" + getUuid() + ", name: '" + getName() + "'}";
    }
}
