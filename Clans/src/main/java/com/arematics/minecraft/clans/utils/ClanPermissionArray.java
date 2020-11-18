package com.arematics.minecraft.clans.utils;

import com.arematics.minecraft.data.mode.model.ClanPermission;

import java.util.ArrayList;
import java.util.Arrays;

public class ClanPermissionArray extends ArrayList<ClanPermission> {

    public static ClanPermissionArray of(String... values){
        ClanPermissionArray clanPermissions = new ClanPermissionArray();
        Arrays.stream(values).forEach(clanPermissions::add);
        return clanPermissions;
    }

    public boolean add(String value) {
        return super.add(ClanPermission.of(value));
    }
}
