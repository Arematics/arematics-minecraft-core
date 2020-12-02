package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.Permission;
import com.arematics.minecraft.data.global.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {

    private final PermissionRepository repository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository){
        this.repository = permissionRepository;
    }

    public Permission findByName(String name){
        Optional<Permission> result = repository.findById(name);
        if(!result.isPresent()) throw new RuntimeException("permission with id: " + name + " could not be found");
        return result.get();
    }

    public void addPermission(Permission permission){
        this.repository.save(permission);
    }
}
