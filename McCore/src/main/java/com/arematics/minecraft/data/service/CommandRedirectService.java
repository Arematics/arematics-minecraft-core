package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.model.CommandRedirect;
import com.arematics.minecraft.data.global.repository.CommandRedirectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommandRedirectService {

    private final Map<String, CommandRedirect> redirectMap;

    @Autowired
    public CommandRedirectService(CommandRedirectRepository commandRedirectRepository){
        this.redirectMap = commandRedirectRepository.findAll()
                .stream()
                .collect(Collectors.toMap(CommandRedirect::getCmd, map -> map));
    }

    public CommandRedirect findRedirect(String cmd){
        return redirectMap.get(cmd.toLowerCase());
    }

    public Map<String, CommandRedirect> getRedirectMap() {
        return redirectMap;
    }
}
