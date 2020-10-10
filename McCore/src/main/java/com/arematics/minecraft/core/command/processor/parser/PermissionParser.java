package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.core.annotations.Parser;
import com.arematics.minecraft.data.service.PermissionService;
import com.arematics.minecraft.data.share.model.Permission;

@Parser
public class PermissionParser extends CommandParameterParser<Permission> {

    @Override
    public Permission doParse(String name) throws ParserException {
        PermissionService service = Boots.getBoot(CoreBoot.class).getContext().getBean(PermissionService.class);
        Permission permission;
        try{
            permission = service.findByName(name);
        }catch (RuntimeException re){
            throw new ParserException(re.getMessage());
        }
        return permission;
    }
}
