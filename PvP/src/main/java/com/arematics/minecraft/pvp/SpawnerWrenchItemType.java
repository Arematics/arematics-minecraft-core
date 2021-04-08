package com.arematics.minecraft.pvp;

import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.messaging.advanced.Part;
import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.server.items.parser.ItemType;
import com.arematics.minecraft.pvp.commands.SpawnerWrenchCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class SpawnerWrenchItemType extends ItemType {

    private final Server server;
    private final SpawnerWrenchCommand spawnerWrenchCommand;

    @Override
    public String propertyValue() {
        return "itemWrench";
    }

    @Override
    public Part execute(CorePlayer player, CoreItem item) {
        try{
            server.items().giveItemTo(player, spawnerWrenchCommand.getWrench());
            return new Part("§6Spawner Wrench");
        }catch (RuntimeException re){
            player.failure("This key could not be found, please report to team").handle();
        }
        throw new RuntimeException("Something went wrong");
    }
}
