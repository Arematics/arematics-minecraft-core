package com.arematics.minecraft.core.listener;

import com.arematics.minecraft.core.server.Server;
import com.arematics.minecraft.core.server.entities.player.CorePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor(onConstructor_=@Autowired)
public class AntiItemDuplicateBugListener implements Listener{

	private final Server server;

	@EventHandler (priority = EventPriority.MONITOR)
	public void onInteract(PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getState() instanceof Dropper || e.getClickedBlock().getState() instanceof Hopper || e.getClickedBlock() instanceof Dispenser){
				final Block block = e.getClickedBlock();
				CorePlayer player = server.fetchPlayer(e.getPlayer());
				server.schedule().syncDelayed(() -> checkBlocks(block, player), 500, TimeUnit.MILLISECONDS);
			}
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent e){
		if(e.isCancelled()) return;
		if(e.getBlockPlaced().getState() instanceof Dropper || e.getBlockPlaced().getState() instanceof Hopper || e.getBlockPlaced() instanceof Dispenser){
			final Block block = e.getBlockPlaced();
			CorePlayer player = server.fetchPlayer(e.getPlayer());
			server.schedule().syncDelayed(() -> checkBlocks(block, player), 500, TimeUnit.MILLISECONDS);
		}
	}

	private void checkBlocks(Block b, CorePlayer p){
		if(b.getState() instanceof Dropper){
			checkDropper(b, p);
		}else if(b.getState() instanceof Dispenser){
			checkDispenser(b, p);
		}else if(b.getState() instanceof Hopper){
			boolean disp = checkDispenser(b, p);
			boolean drop = !disp && checkDropper(b, p);
		}
	}

	private static boolean checkDispenser(Block b, CorePlayer p){
		final BlockFace[] faces = new BlockFace[]{
				BlockFace.NORTH,
				BlockFace.EAST,
				BlockFace.SOUTH,
				BlockFace.WEST,
				BlockFace.UP,
				BlockFace.DOWN
		};

		if(b.getState() instanceof Dispenser){
			Dispenser dispenser = (Dispenser) b.getState();

			Hopper hopper = null;

			for(BlockFace face : faces){
				if(b.getRelative(face).getState() instanceof Hopper)
					hopper = (Hopper) b.getRelative(face).getState();
			}

			if(hopper == null) return false;

			BlockFace dface = faceNums.get((int) dispenser.getBlock().getData());
			BlockFace hface = faceNums.get((int) hopper.getBlock().getData());

			if(dface == null || hface == null) return false;

			if(dface != opposites.get(hface)) return false;

			p.failure("This bug is forbidden and can be punished with a ban. So don't even try it!!!").handle();
			dispenser.setType(Material.AIR);
			dispenser.update(true);
			hopper.setType(Material.AIR);
			hopper.update(true);

			hopper.getWorld().playEffect(hopper.getLocation().add(0.5, 0.5, 0.5), Effect.ENDER_SIGNAL, 0);
			dispenser.getWorld().playEffect(dispenser.getLocation().add(0.5, 0.5, 0.5), Effect.ENDER_SIGNAL, 0);
			hopper.getWorld().playSound(hopper.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
			dispenser.getWorld().playSound(dispenser.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);

			return true;

		}else if(b.getState() instanceof Hopper){
			Hopper hopper = (Hopper) b.getState();

			Dispenser dispenser = null;

			for(BlockFace face : faces){
				if(b.getRelative(face).getState() instanceof Dispenser)
					dispenser = (Dispenser) b.getRelative(face).getState();
			}

			if(dispenser == null) return false;

			BlockFace dface = faceNums.get((int) dispenser.getBlock().getData());
			BlockFace hface = faceNums.get((int) hopper.getBlock().getData());

			if(dface == null || hface == null) return false;

			if(dface != opposites.get(hface)) return false;

			p.failure("This bug is forbidden and can be punished with a ban. So don't even try it!!!").handle();
			dispenser.setType(Material.AIR);
			dispenser.update(true);
			hopper.setType(Material.AIR);
			hopper.update(true);

			hopper.getWorld().playEffect(hopper.getLocation().add(0.5, 0.5, 0.5), Effect.ENDER_SIGNAL, 0);
			dispenser.getWorld().playEffect(dispenser.getLocation().add(0.5, 0.5, 0.5), Effect.ENDER_SIGNAL, 0);
			hopper.getWorld().playSound(hopper.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
			dispenser.getWorld().playSound(dispenser.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);

			return true;

		}

		return false;
	}

	private static boolean checkDropper(Block b, CorePlayer p){
		final BlockFace[] faces = new BlockFace[]{
				BlockFace.NORTH,
				BlockFace.EAST,
				BlockFace.SOUTH,
				BlockFace.WEST,
				BlockFace.UP,
				BlockFace.DOWN
		};

		if(b.getState() instanceof Dropper){
			Dropper dropper = (Dropper) b.getState();

			Hopper hopper = null;

			for(BlockFace face : faces){
				if(b.getRelative(face).getState() instanceof Hopper)
					hopper = (Hopper) b.getRelative(face).getState();
			}

			if(hopper == null) return false;

			BlockFace dface = faceNums.get(Integer.valueOf(dropper.getBlock().getData()));
			BlockFace hface = faceNums.get(Integer.valueOf(hopper.getBlock().getData()));

			if(dface == null || hface == null) return false;

			if(dface != opposites.get(hface)) return false;

			p.failure("This bug is forbidden and can be punished with a ban. So don't even try it!!!").handle();
			dropper.setType(Material.AIR);
			dropper.update(true);
			hopper.setType(Material.AIR);
			hopper.update(true);

			hopper.getWorld().playEffect(hopper.getLocation().add(0.5, 0.5, 0.5), Effect.ENDER_SIGNAL, 0);
			dropper.getWorld().playEffect(dropper.getLocation().add(0.5, 0.5, 0.5), Effect.ENDER_SIGNAL, 0);
			hopper.getWorld().playSound(hopper.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
			dropper.getWorld().playSound(dropper.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);

			return true;

		}else if(b.getState() instanceof Hopper){
			Hopper hopper = (Hopper) b.getState();

			Dropper dropper = null;

			for(BlockFace face : faces){
				if(b.getRelative(face).getState() instanceof Dropper)
					dropper = (Dropper) b.getRelative(face).getState();
			}

			if(dropper == null) return false;

			BlockFace dface = faceNums.get(Integer.valueOf(dropper.getBlock().getData()));
			BlockFace hface = faceNums.get(Integer.valueOf(hopper.getBlock().getData()));

			if(dface == null || hface == null) return false;

			if(dface != opposites.get(hface)) return false;

			p.failure("This bug is forbidden and can be punished with a ban. So don't even try it!!!").handle();
			dropper.setType(Material.AIR);
			dropper.update(true);
			hopper.setType(Material.AIR);
			hopper.update(true);

			hopper.getWorld().playEffect(hopper.getLocation().add(0.5, 0.5, 0.5), Effect.ENDER_SIGNAL, 0);
			dropper.getWorld().playEffect(dropper.getLocation().add(0.5, 0.5, 0.5), Effect.ENDER_SIGNAL, 0);
			hopper.getWorld().playSound(hopper.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
			dropper.getWorld().playSound(dropper.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);

			return true;

		}

		return false;

	}

	private static final HashMap<BlockFace, BlockFace> opposites = new HashMap<BlockFace, BlockFace>(){{
		put(BlockFace.UP, BlockFace.DOWN);
		put(BlockFace.DOWN, BlockFace.UP);
		put(BlockFace.NORTH, BlockFace.SOUTH);
		put(BlockFace.SOUTH, BlockFace.NORTH);
		put(BlockFace.WEST, BlockFace.EAST);
		put(BlockFace.EAST, BlockFace.WEST);
	}};

	private static final HashMap<Integer, BlockFace> faceNums = new HashMap<Integer, BlockFace>(){{
		put(0, BlockFace.DOWN);
		put(1, BlockFace.UP);
		put(2, BlockFace.NORTH);
		put(3, BlockFace.SOUTH);
		put(4, BlockFace.WEST);
		put(5, BlockFace.EAST);
	}};

}
