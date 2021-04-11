package com.arematics.minecraft.data.service;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.ClanMember;
import com.arematics.minecraft.data.mode.repository.ClanMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "clanMembers")
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class ClanMemberService {

    private final ClanMemberRepository clanMemberRepository;

    @Cacheable(key = "#uuid")
    public ClanMember getMember(UUID uuid){
        Optional<ClanMember> clanMember = clanMemberRepository.findById(uuid);
        if(!clanMember.isPresent())
            throw new RuntimeException("ClanMember with uuid: " + uuid.toString() + " could not be found");
        return clanMember.get();
    }

    public ClanMember getMember(CorePlayer player){
        return getMember(player.getUUID());
    }

    @CachePut(key = "#result.uuid")
    public ClanMember update(ClanMember member){
        return clanMemberRepository.save(member);
    }

    @CacheEvict(key = "#member.uuid")
    public void delete(ClanMember member){
        clanMemberRepository.delete(member);
    }

    @CacheEvict(key = "#member.uuid")
    public void evictCache(ClanMember member){}
}
