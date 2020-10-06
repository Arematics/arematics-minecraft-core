package com.arematics.minecraft.data.service;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.data.mode.repository.ModePermissionsRepository;
import com.arematics.minecraft.data.share.model.Permission;
import com.arematics.minecraft.data.share.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Service
public class PermissionService {

    private final PermissionRepository repository;
    private final ModePermissionsRepository modeRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository, ModePermissionsRepository modePermissionsRepository){
        this.repository = permissionRepository;
        this.modeRepository = modePermissionsRepository;
    }

    public Permission findByName(String name){

        Optional<Permission> permissionMode = modeRepository.findById(name);
        if(permissionMode.isPresent()) return permissionMode.get();
        modeRepository.findAll().forEach(System.out::println);

        Optional<Permission> permissionGlobal = repository.findById(name);
        if(!permissionGlobal.isPresent()) throw new RuntimeException("permission with id: " + name + " could not be found");
        return permissionGlobal.get();
    }

    public void addPermission(Permission permission){
        this.repository.save(permission);
    }
}
