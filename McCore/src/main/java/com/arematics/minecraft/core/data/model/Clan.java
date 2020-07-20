package com.arematics.minecraft.core.data.model;

import com.arematics.minecraft.core.annotations.DataCacheService;
import com.arematics.minecraft.core.annotations.DataUnusedCheck;
import com.arematics.minecraft.core.data.TimeType;

import javax.persistence.*;
import java.lang.reflect.Member;
import java.util.List;

@Entity
@Table(name = "clans")
@DataCacheService(async = true)
@DataUnusedCheck(timeType = TimeType.MONTH, value = 3)
public class Clan {
    @Id
    private Integer clanId;
    private String name;
    private String tag;
    private String colorCode;
    private int slots;
    private int level;
    private int kills;
    private int deaths;
    private double money;
    @JoinTable(name = "clan_members", joinColumns = @JoinColumn(name = "clan_id"))
    private List<Member> members;
    @JoinTable(name = "clan_rangs", joinColumns = @JoinColumn(name = "clan_id"))
    private List<ClanRang> clanRangs;
}
