package com.arematics.minecraft.core.data.service;

import com.arematics.minecraft.core.data.model.ArematicsAccount;
import com.arematics.minecraft.core.data.repository.ArematicsAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ArematicsAccountService {

    private final ArematicsAccountRepository repository;

    @Autowired
    public ArematicsAccountService(ArematicsAccountRepository repository){
        this.repository = repository;
    }

    public ArematicsAccount findBySoulConnection(UUID soulConnection){
        Optional<ArematicsAccount> account = this.repository.findArematicsAccountBySoulConnection(soulConnection);
        if(!account.isPresent()) throw new RuntimeException("Account not found");
        return account.get();
    }

    public ArematicsAccount findOrCreateBySoulConnection(UUID soulConnection){
        try{
            return findBySoulConnection(soulConnection);
        }catch (RuntimeException re){
            return repository.save(new ArematicsAccount(UUID.randomUUID(), soulConnection, false));
        }
    }


}
