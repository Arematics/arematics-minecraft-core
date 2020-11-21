package com.arematics.minecraft.clans.utils;

import com.arematics.minecraft.data.mode.model.ClanMember;
import com.arematics.minecraft.data.mode.model.ClanPermission;

import java.util.stream.Stream;

public class ClanPermissions {

    private static Stream<String> fetchPerms(ClanMember member){
        return member.getRank().getPermissions().stream()
                .map(ClanPermission::getPermission);
    }

    public static boolean isAdmin(ClanMember member){
        return fetchPerms(member).anyMatch(permission -> permission.equals("admin"));
    }

    public static boolean canInvite(ClanMember member){
        return fetchPerms(member).anyMatch(permission -> permission.equals("admin") || permission.equals("invite"));
    }

    public static boolean canKick(ClanMember member){
        return fetchPerms(member).anyMatch(permission -> permission.equals("admin") || permission.equals("remove"));
    }

    public static boolean canUseMoney(ClanMember member){
        return fetchPerms(member).anyMatch(permission -> permission.equals("admin") || permission.equals("use-money"));
    }

    public static boolean rankLevelCorrect(ClanMember member, ClanMember target){
        return member.getRank().getRankLevel() < target.getRank().getRankLevel();
    }
}
