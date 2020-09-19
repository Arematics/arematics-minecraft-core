package com.arematics.minecraft.core.data.service;

import com.arematics.minecraft.core.data.model.Permission;
import com.arematics.minecraft.core.data.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {

    private final PermissionRepository repository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository){
        this.repository = permissionRepository;
    }

    @Cacheable(cacheNames = "permCache")
    public Permission findByName(String name){
        Optional<Permission> permission = repository.findById(name);
        if(!permission.isPresent()) throw new RuntimeException("permission with id: " + name + " could not be found");
        return permission.get();
    }

    @CachePut(cacheNames = "permCache")
    public void addPermission(Permission permission){
        this.repository.save(permission);
    }
}
