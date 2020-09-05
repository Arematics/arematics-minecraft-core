package com.arematics.minecraft.core.data.service;

import com.arematics.minecraft.core.data.model.ArematicsAccount;
import com.arematics.minecraft.core.data.repository.ArematicsAccountRepository;
import com.arematics.minecraft.core.items.CoreItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
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
        System.out.println(account.isPresent());
        if(!account.isPresent()) throw new RuntimeException("Account not found");
        return account.get();
    }

    public ArematicsAccount findOrCreateBySoulConnection(UUID soulConnection){
        try{
            return findBySoulConnection(soulConnection);
        }catch (RuntimeException re){
            ItemStack itemStack = new ItemStack(Material.DIAMOND);
            CoreItem item = CoreItem.create(itemStack);
            item.setString("key", new Random().nextInt(2424) + "Hallo");
            return repository.save(new ArematicsAccount(UUID.randomUUID(), soulConnection, false,
                    new CoreItem[]{item}));
        }
    }


}
