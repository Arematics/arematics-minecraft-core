package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.repository.ModeBroadcastMessageRepository;
import com.arematics.minecraft.data.share.model.BroadcastMessage;
import com.arematics.minecraft.data.share.repository.BroadcastMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class BroadcastService {

    private final Random random = new Random(Short.MAX_VALUE);
    private final BroadcastMessageRepository repository;
    private final ModeBroadcastMessageRepository modeBroadcastMessageRepository;

    @Autowired
    public BroadcastService(BroadcastMessageRepository broadcastMessageRepository,
                            ModeBroadcastMessageRepository modeBroadcastMessageRepository){
        this.repository = broadcastMessageRepository;
        this.modeBroadcastMessageRepository = modeBroadcastMessageRepository;
    }

    public BroadcastMessage fetchRandom(){
        List<BroadcastMessage> globals = repository.findAll();
        globals.addAll(modeBroadcastMessageRepository.findAll());
        if(globals.size() == 0) throw new RuntimeException("No broadcast message found");
        Collections.shuffle(globals, random);
        return globals.get(0);
    }
}
