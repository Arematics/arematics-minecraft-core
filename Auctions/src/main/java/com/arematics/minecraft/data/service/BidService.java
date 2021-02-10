package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.mode.model.Bid;
import com.arematics.minecraft.data.mode.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class BidService {
    private final BidRepository bidRepository;

    public Bid save(Bid bid){
        return bidRepository.save(bid);
    }
}
