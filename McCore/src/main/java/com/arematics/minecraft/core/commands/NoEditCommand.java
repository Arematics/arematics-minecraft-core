package com.arematics.minecraft.core.commands;

import com.arematics.minecraft.core.annotations.Perm;
import com.arematics.minecraft.core.annotations.SubCommand;
import com.arematics.minecraft.core.command.CoreCommand;
import com.arematics.minecraft.core.command.processor.parser.CommandProcessException;
import com.arematics.minecraft.core.items.CoreItem;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Perm(permission = "team.noedit", description = "Permission for no edit command")
public class NoEditCommand extends CoreCommand {

    private static Map<UUID, CoreItem> noEdit = new HashMap<>();

    public NoEditCommand() {
        super("noedit", "nedit");
    }

    public Map<UUID, CoreItem> getNoEdit() {
        return noEdit;
    }

    @Override
    public void onDefaultExecute(CorePlayer sender) {
        boolean contains = noEdit.containsKey(sender.getUUID());
        if(contains) noEdit.remove(sender.getUUID());
        else noEdit.put(sender.getUUID(), null);
        sender.info("No Edit wurde " + (contains ? "deaktiviert" : "aktiviert")).handle();
    }

    @SubCommand("hand")
    public void addNoEditForHand(CorePlayer sender) {
        boolean contains = noEdit.containsKey(sender.getUUID());
        if(contains) noEdit.remove(sender.getUUID());
        CoreItem hand  = sender.getItemInHand();
        if(hand == null) throw new CommandProcessException("no_item_in_hand");
        noEdit.put(sender.getUUID(), hand);
        sender.info("No Edit wurde auf das aktuelle Item in deiner Hand aktiviert. Deaktivieren mit /noedit").handle();
    }
}
