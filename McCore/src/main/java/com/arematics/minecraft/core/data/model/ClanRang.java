package com.arematics.minecraft.core.data.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "clan_rangs")
public class ClanRang {
    @Id
    private String rangName;
    private int clanId;
}
