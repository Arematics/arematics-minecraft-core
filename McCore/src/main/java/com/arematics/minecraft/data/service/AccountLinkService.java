package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.AccountLink;
import com.arematics.minecraft.data.global.model.AccountLinkId;
import com.arematics.minecraft.data.global.repository.AccountLinkRepository;
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
@CacheConfig(cacheManager = "globalCache")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AccountLinkService {
    private final AccountLinkRepository accountlinkrepository;

    public AccountLinkId of(UUID one, UUID two){
        return new AccountLinkId(one, two);
    }

    public boolean isLinkExistsBoth(UUID one, UUID two){
        return isLinked(of(one, two)) || isLinked(of(two, one));
    }

    public AccountLink findOneOfBoth(UUID one, UUID two){
        try{
            return findAccountLink(of(one, two));
        }catch (Exception e){
            return findAccountLink(of(two, one));
        }
    }

    @Cacheable(cacheNames = "accounts_linked", key = "#id.userOne.toString() + #id.userTwo.toString()")
    public boolean isLinked(AccountLinkId id){
        return accountlinkrepository.existsAccountLinkByUserOneAndUserTwo(id.getUserOne(), id.getUserTwo());
    }

    @Cacheable(cacheNames = "accountlink", key = "#id.userOne.toString() + #id.userTwo.toString()")
    public AccountLink findAccountLink(AccountLinkId id) {
        Optional<AccountLink> result = accountlinkrepository.findById(id);
        if (!result.isPresent())
            throw new RuntimeException("Change this");
        return result.get();
    }

    @CachePut(cacheNames = "accountlink", key = "#result.userOne.toString() + #result.userTwo.toString()")
    public AccountLink save(AccountLink accountlink) {
        return accountlinkrepository.save(accountlink);
    }

    @CacheEvict(cacheNames = {"accountlink", "accounts_linked"}, key = "#accountlink.userOne.toString() + #accountlink.userTwo.toString()")
    public void remove(AccountLink accountlink) {
        accountlinkrepository.delete(accountlink);
    }
}
