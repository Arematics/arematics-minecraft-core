package com.arematics.minecraft.data.service;

import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.mode.model.ClanMember;
import com.arematics.minecraft.data.mode.repository.ClanMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ClanMemberService {

    private ClanMemberRepository repository;

    @Autowired
    public ClanMemberService(ClanMemberRepository clanMemberRepository){
        this.repository = clanMemberRepository;
    }

    @Cacheable(cacheNames = "clanMembers")
    public ClanMember getMember(UUID uuid){
        Optional<ClanMember> clanMember = repository.findById(uuid);
        if(!clanMember.isPresent())
            throw new RuntimeException("ClanMember with uuid: " + uuid.toString() + " could not be found");
        return clanMember.get();
    }

    public ClanMember getMember(CorePlayer player){
        return getMember(player.getUUID());
    }

    @CachePut(cacheNames = "clanMembers")
    public ClanMember update(ClanMember member){
        return repository.save(member);
    }

    @CacheEvict(cacheNames = "clanMembers")
    public void delete(ClanMember member){
        repository.delete(member);
    }
}
