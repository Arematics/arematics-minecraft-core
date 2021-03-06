package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.Ignored;
import com.arematics.minecraft.data.global.repository.IgnoredRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class IgnoredService {

    private final IgnoredRepository repository;

    @Autowired
    public IgnoredService(IgnoredRepository ignoredRepository){
        this.repository = ignoredRepository;
    }

    public Page<Ignored> fetchAllIgnored(UUID ignorer, int page){
        return repository.findAllByIgnorer(ignorer, PageRequest.of(page, 28));
    }

    public boolean hasIgnored(UUID ignorer, UUID ignored){
        return repository.existsByIgnorerAndIgnored(ignorer, ignored);
    }

    public void ignore(UUID ignorer, UUID ignored){
        repository.save(new Ignored(null, ignorer, ignored));
    }

    public void unIgnore(UUID ignorer, UUID ignored){
        Optional<Ignored> data = repository.findByIgnorerAndIgnored(ignorer, ignored);
        data.ifPresent(repository::delete);
    }
}
