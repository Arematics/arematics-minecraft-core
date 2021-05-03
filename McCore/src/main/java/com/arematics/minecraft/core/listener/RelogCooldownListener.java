package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.Server;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class RelogCooldownListener implements Listener {

	private final Server server;
	private final HashMap<UUID, LocalDateTime> justOnline = new HashMap<>();
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		UUID uuid = e.getPlayer().getUniqueId();
		justOnline.put(uuid, LocalDateTime.now().plusSeconds(3));
		server.schedule().asyncDelayed(() -> justOnline.remove(uuid), 3, TimeUnit.SECONDS);
	}
	
	@EventHandler
	public void onAsyncJoin(AsyncPlayerPreLoginEvent e){
		UUID uuid = e.getUniqueId();
		if(!justOnline.containsKey(uuid)) return;
		if(justOnline.get(uuid).isAfter(LocalDateTime.now()))
			e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§ePlease wait");
	}
	
}
