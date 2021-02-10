package com.arematics.minecraft.data.service;

import com.arematics.minecraft.data.global.repository.RankAuctionPermissionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class RankAuctionPermissionsService {
    private final RankAuctionPermissionsRepository rankAuctionPermissionsRepository;
}
