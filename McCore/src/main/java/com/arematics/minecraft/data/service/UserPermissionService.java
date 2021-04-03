package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.repository.ModeUserPermissionRepository;
import com.arematics.minecraft.data.share.repository.UserPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;
    private final ModeUserPermissionRepository modeUserPermissionRepository;

    @Autowired
    public UserPermissionService(UserPermissionRepository userPermissionRepository,
                                 ModeUserPermissionRepository modeUserPermissionRepository){
        this.userPermissionRepository = userPermissionRepository;
        this.modeUserPermissionRepository = modeUserPermissionRepository;
    }

    @Cacheable(cacheNames = "userPermissions", key = "#uuid + #permission")
    public boolean hasPermission(UUID uuid, String permission){
        return checkWildcardsAndPerm(uuid, permission);
    }

    private boolean hasPerm(UUID uuid, String permission){
        return hasPerm(uuid, permission, userPermissionRepository) ||
                hasPerm(uuid, permission, modeUserPermissionRepository);
    }

    private boolean hasPerm(UUID uuid, String permission, UserPermissionRepository userPermissionRepository){
        return userPermissionRepository
                .hasPerm(uuid, permission, Timestamp.valueOf(LocalDateTime.now()));
    }

    private boolean checkWildcardsAndPerm(UUID uuid, String permission){
        if(hasPerm(uuid, "*")) return true;
        if(hasPerm(uuid, permission)) return true;
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
