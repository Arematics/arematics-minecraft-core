package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.Rank;
import com.arematics.minecraft.data.mode.repository.ModeRankPermissionRepository;
import com.arematics.minecraft.data.share.repository.RanksPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class RankPermissionService {

    private final RanksPermissionRepository ranksPermissionRepository;
    private final ModeRankPermissionRepository modeRankPermissionRepository;

    @Autowired
    public RankPermissionService(RanksPermissionRepository ranksPermissionRepository,
                                 ModeRankPermissionRepository modeRankPermissionRepository){
        this.ranksPermissionRepository = ranksPermissionRepository;
        this.modeRankPermissionRepository = modeRankPermissionRepository;
    }


    @Cacheable(cacheNames = "rankPermissions", key = "#rank.id + #permission")
    public boolean hasPermission(Rank rank, String permission){
        return checkWildcardsAndPerm(rank, permission);
    }

    private boolean hasPerm(Rank rank, String permission){
        return hasPerm(rank, permission, ranksPermissionRepository) ||
                hasPerm(rank, permission, modeRankPermissionRepository);
    }

    private boolean hasPerm(Rank rank, String permission, RanksPermissionRepository ranksPermissionRepository){
        return ranksPermissionRepository.hasPerm(
                rank.getId(), permission, Timestamp.valueOf(LocalDateTime.now()));
    }

    private boolean checkWildcardsAndPerm(Rank rank, String permission){
        if(hasPerm(rank, "*")) return true;
        if(hasPerm(rank, permission)) return true;
        int lastIndex = 0;
        while(lastIndex != -1){
            lastIndex = permission.indexOf(".", lastIndex);
            String perm = lastIndex != -1 ? permission.substring(0, lastIndex) + ".*" : permission;
            if(lastIndex != -1) lastIndex++;
            if(hasPerm(rank, perm)) return true;
        }
        return false;
    }
}
