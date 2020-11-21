package com.arematics.minecraft.core.command.processor.parser;

import com.arematics.minecraft.core.Boots;
import com.arematics.minecraft.core.CoreBoot;
import com.arematics.minecraft.data.service.PermissionService;
import com.arematics.minecraft.data.share.model.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionParser extends CommandParameterParser<Permission> {

    @Override
    public Permission parse(String name) throws CommandProcessException {
        if(name.equals("null")) return null;
        PermissionService service = Boots.getBoot(CoreBoot.class).getContext().getBean(PermissionService.class);
        Permission permission;
        try{
            permission = service.findByName(name);
        }catch (RuntimeException re){
            throw new CommandProcessException(re.getMessage());
        }
        return permission;
    }
}
