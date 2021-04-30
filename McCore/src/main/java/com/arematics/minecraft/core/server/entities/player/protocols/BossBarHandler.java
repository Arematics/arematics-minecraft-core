package com.arematics.minecraft.core.server.entities.player.protocols;

import com.arematics.minecraft.core.messaging.injector.StringInjector;
import com.arematics.minecraft.core.server.entities.bossbar.BossBar;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import com.arematics.minecraft.core.utils.ArematicsExecutor;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class BossBarHandler {

    private static final List<BossBarHandler> handlers = new ArrayList<>();

    static{
        ArematicsExecutor.asyncRepeat(() -> handlers.forEach(handler -> ArematicsExecutor.syncRun(handler::refreshLocation)),
                0, 5, TimeUnit.SECONDS);
    }


    private final CorePlayer player;
    private BossBar bossBar;

    public BossBarHandler(CorePlayer player){
        this.player = player;
        handlers.add(this);
    }

    public void sendNormal(String msg, Consumer<StringInjector> consumer){
        sendWithLength(msg, consumer, 5);
    }

    public void sendWithLength(String msg, Consumer<StringInjector> consumer, int maxSize){
        ArematicsExecutor.syncRepeat(count -> {
            if(count == 0) hide();
            else {
                float value = ((float) count) / ((float) maxSize);
                System.out.println(value);
                set(msg, consumer, value);
            }
        }, 1, 1, TimeUnit.SECONDS, maxSize);
    }

    public void show(){
        if (bossBar == null) bossBar = change("null", 1F);
        respawn();
    }

    public void hide(){
        if (bossBar != null && bossBar.isSpawned()) despawnCurrent();
    }

    public void setText(StringInjector injector){
        String text = injector.toObjectForFirst();
        if (bossBar == null) bossBar = change(text, 1F);
        else bossBar.setText(text);
        refresh();
    }

    public void setPercent(float percent){
        if (bossBar == null) bossBar = change("null", percent);
        else bossBar.setHealth(convertToHealth(percent));
        refresh();
    }

    public void set(String text, float percent){
        if (bossBar == null) {
            bossBar = change(text, percent);
        }
        else {
            bossBar.setText(text);
            bossBar.setHealth(convertToHealth(percent));
        }
        respawn();
    }

    public void set(String msg, Consumer<StringInjector> consumer, float percent){
        StringInjector injector = player.info(msg).DEFAULT().disableServerPrefix();
        consumer.accept(injector);
        String text = injector.toObjectForFirst();
        if (bossBar == null) {
            bossBar = change(text, percent);
        }
        else {
            bossBar.setText(text);
            bossBar.setHealth(convertToHealth(percent));
        }
        respawn();
    }

    public BossBar change(String text, float percent){
        return bossBar = new BossBar(player, text, convertToHealth(percent), null, false);
    }

    private void respawn(){
        hide();
        spawnCurrent();
    }

    private void refresh(){
        if (bossBar.isSpawned()) render();
        else spawnCurrent();
    }

    private int convertToHealth(float percent) {
        return (int) Math.floor(percent * 300.0F);
    }

    private Location getDistantLocation() {
        Location location = player.getPlayer().getEyeLocation().clone();
        location.setPitch(location.getPitch() - 21);

        return location.add(location.getDirection().normalize().multiply(50));
    }

    private void render() {
        if(bossBar == null) return;
        PacketContainer metadataPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metadataPacket.getModifier().write(0, BossBar.CUSTOM_ID);
        metadataPacket.getWatchableCollectionModifier().write(0, bossBar.getDataWatcher().getWatchableObjects());

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player.player(), metadataPacket, false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void teleport() {
        if(bossBar == null) return;
        PacketContainer teleportPacket = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        StructureModifier<Object> teleportPacketModifier = teleportPacket.getModifier();

        teleportPacketModifier.write(0, BossBar.CUSTOM_ID);
        teleportPacketModifier.write(1,  (bossBar.getLocation().getBlockX() * 32));
        teleportPacketModifier.write(2,  (bossBar.getLocation().getBlockY() * 32));
        teleportPacketModifier.write(3,  (bossBar.getLocation().getBlockZ() * 32));
        teleportPacketModifier.write(4, (byte) (bossBar.getLocation().getYaw() * 256 / 360));
        teleportPacketModifier.write(5, (byte) (bossBar.getLocation().getPitch() * 256 / 360));
        teleportPacketModifier.write(6, true);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player.player(), teleportPacket, false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void refreshLocation(){
        if(bossBar == null) return;
        bossBar.setLocation(getDistantLocation());
        teleport();
    }

    /**
     * Send spawning packets for entity with boss bar metadata
     */
    private void spawnCurrent() {
        if(bossBar == null) return;
        Location location = getDistantLocation();
        bossBar.setSpawned(true);
        bossBar.setLocation(location);

        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        StructureModifier<Object> spawnPacketModifier = spawnPacket.getModifier();
        spawnPacketModifier.write(0, BossBar.CUSTOM_ID);
        spawnPacketModifier.write(1, (byte) 64);
        spawnPacketModifier.write(2, bossBar.getLocation().getBlockX() * 32);
        spawnPacketModifier.write(3, bossBar.getLocation().getBlockY() * 32);
        spawnPacketModifier.write(4, bossBar.getLocation().getBlockZ() * 32);

        spawnPacket.getDataWatcherModifier().write(0, bossBar.getDataWatcher());

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player.player(), spawnPacket, false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Destroy current bossBar Entity removing boss bar
     */
    private void despawnCurrent() {
        if(bossBar == null) return;
        bossBar.setSpawned(false);
        bossBar.setLocation(null);

        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        spawnPacket.getIntegerArrays().write(0, new int[] { BossBar.CUSTOM_ID });

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player.player(), spawnPacket, false);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
