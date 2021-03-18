package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.Friend;
import com.arematics.minecraft.data.global.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class FriendService {

    private final FriendRepository repository;

    public Page<Friend> fetchAllFriends(UUID uuid, int page){
        return repository.findAllByUuidOrTargetUuid(uuid, uuid, PageRequest.of(page, 28));
    }

    public boolean isFriended(UUID uuid, UUID friended){
        return repository.existsByUuidAndTargetUuid(uuid, friended) || repository.existsByUuidAndTargetUuid(friended, uuid);
    }

    public Friend getFriend(UUID uuid, UUID target){
        Optional<Friend> resultOne = repository.findByUuidAndTargetUuid(uuid, target);
        return resultOne.orElseGet(() -> repository.findByUuidAndTargetUuid(target, uuid)
                .orElseThrow(() -> new RuntimeException("No friend found")));
    }

    public void friend(UUID uuid, UUID friended){
        repository.save(new Friend(uuid, friended));
    }

    public void unFriend(Friend friend){
        repository.delete(friend);
    }
}
