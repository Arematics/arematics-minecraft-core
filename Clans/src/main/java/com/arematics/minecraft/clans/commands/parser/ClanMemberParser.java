package com.arematics.minecraft.clans.commands.parser;

import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.command.processor.parser.CommandSenderParser;
import com.arematics.minecraft.core.command.processor.parser.UserParser;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.data.global.model.User;
import com.arematics.minecraft.data.mode.model.ClanMember;
import com.arematics.minecraft.data.service.ClanMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClanMemberParser extends CommandSenderParser<ClanMember> {

    private final ClanMemberService service;
    private final UserParser userParser;

    @Autowired
    public ClanMemberParser(ClanMemberService clanMemberService, UserParser userParser){
        this.service = clanMemberService;
        this.userParser = userParser;
    }

    @Override
    public ClanMember parse(String value) throws CommandProcessException {
        try{
            User user = this.userParser.parse(value);
            return service.getMember(user.getUuid());
        }catch (RuntimeException re){
            re.printStackTrace();
            throw new CommandProcessException("Player: " + value + " has no clan");
        }
    }

    @Override
    public ClanMember parse(CorePlayer sender) throws CommandProcessException {
        try{
            return parse(sender.getName());
        }catch (CommandProcessException cpe){
            throw new CommandProcessException("You have no clan");
        }
    }
}
