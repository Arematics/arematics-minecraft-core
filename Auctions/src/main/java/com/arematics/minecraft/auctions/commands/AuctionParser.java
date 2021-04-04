package com.arematics.minecraft.auctions.commands;

import com.arematics.minecraft.core.command.processor.parser.CommandParameterParser;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.data.mode.model.Auction;
import com.arematics.minecraft.data.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class AuctionParser extends CommandParameterParser<Auction> {

    private final AuctionService auctionService;

    @Override
    public Auction parse(String value) throws CommandProcessException {
        try{
            return auctionService.findById(Long.parseLong(value));
        }catch (Exception e){
            throw new CommandProcessException("Could not find auction with id: " + value);
        }
    }
}
