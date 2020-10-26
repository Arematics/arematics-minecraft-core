package com.arematics.minecraft.kits.parser;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.command.processor.parser.CommandParameterParser;
import com.arematics.minecraft.core.command.processor.parser.ParserException;
import com.arematics.minecraft.data.mode.model.Kit;
import com.arematics.minecraft.data.service.KitService;

public class KitParser extends CommandParameterParser<Kit> {

    @Override
    public Kit doParse(String name) throws ParserException {
        KitService service = Boots.getBoot(CoreBoot.class).getContext().getBean(KitService.class);
        Kit kit;
        try{
            kit = service.findKit(name);
        }catch (RuntimeException re){
            throw new ParserException(re.getMessage());
        }
        return kit;
    }
}
