package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.data.global.model.Rank;
import com.arematics.minecraft.data.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class RankParser extends CommandParameterParser<Rank> {

    private final RankService rankService;

    @Override
    public Rank parse(String value) throws CommandProcessException {
        try{
            return rankService.findByName(value);
        }catch (Exception e){
            throw new CommandProcessException("Rank does not exists");
        }
    }
}
