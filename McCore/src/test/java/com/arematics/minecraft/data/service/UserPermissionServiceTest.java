package com.arematics.minecraft.data.service;

import com.arematics.minecraft.core.permissions.Permissions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserPermissionServiceTest extends Assertions {

    private UUID uuid;
    private List<String> permissions;

    @BeforeEach
    public void setUp() {
        uuid = UUID.randomUUID();
        permissions = new ArrayList<>();
        permissions.add("test.check");
    }

    @Test
    void hasPermission() {
        assertTrue(checkWildcardsAndPerm(uuid, "test.check"), "User don't have correct permission");
        permissions.remove("test.check");
        permissions.add("test");
        assertTrue(checkWildcardsAndPerm(uuid, "test"), "User don't have correct permission");
        assertFalse(checkWildcardsAndPerm(uuid, "test.hase"), "User don't have correct permission");
        assertTrue(Permissions.hasPermission(uuid, "test"), "Permission check failed");
        assertFalse(Permissions.hasPermission(uuid, "test.hase"), "Permission check failed");
        permissions.remove("test");
        assertFalse(checkWildcardsAndPerm(uuid, "test.check"), "User should have permission here");
        assertFalse(checkWildcardsAndPerm(uuid, "test.failure"), "User should have permission here");
        assertFalse(Permissions.hasPermission(uuid, "test.check"), "Permission check failed");
        assertFalse(Permissions.hasPermission(uuid, "test.failure"), "Permission check failed");
        permissions.add("test.*");
        assertTrue(checkWildcardsAndPerm(uuid, "test.check"), "Second level wildcard failed");
        assertTrue(checkWildcardsAndPerm(uuid, "test.failure"), "Second level wildcard failed");
        assertTrue(Permissions.hasPermission(uuid, "test.check"), "Permission check failed");
        assertTrue(Permissions.hasPermission(uuid, "test.failure"), "Permission check failed");
        permissions.remove("test.*");
        assertFalse(checkWildcardsAndPerm(uuid, "test.check"), "User should have permission here");
        permissions.add("*");
        assertTrue(checkWildcardsAndPerm(uuid, "test.check"), "all wildcard failed");
        assertTrue(checkWildcardsAndPerm(uuid, "test.failure"), "all wildcard failed");
        assertTrue(checkWildcardsAndPerm(uuid, "test.check.failure"), "all wildcard failed");
        assertTrue(Permissions.hasPermission(uuid, "test.check.failure"), "Permission check failed");
    }

    private boolean hasPerm(UUID uuid, String permission){
        return permissions.contains(permission);
    }

    private boolean checkWildcardsAndPerm(UUID uuid, String permission){
        if(hasPerm(uuid, "*")) return true;
        int lastIndex = 0;
        while(lastIndex != -1){
            lastIndex = permission.indexOf(".", lastIndex);
            String perm = lastIndex != -1 ? permission.substring(0, lastIndex) + ".*" : permission;
            if(lastIndex != -1) lastIndex++;
            if(hasPerm(uuid, perm)) return true;
        }
        return false;
    }
}
