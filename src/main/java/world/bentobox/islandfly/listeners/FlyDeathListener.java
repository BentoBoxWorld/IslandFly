package world.bentobox.islandfly.listeners;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.islandfly.IslandFlyAddon;

public class FlyDeathListener implements Listener {
	
    private final BentoBox plugin;
    
    public FlyDeathListener(final IslandFlyAddon addon){
        this.plugin = addon.getPlugin();
    }

	@EventHandler
	public void onDeath(final PlayerDeathEvent event) {
		//Disable fly on death anyway
		final Player player = event.getEntity();
		final UUID playerUUID = player.getUniqueId();
		final User user = User.getInstance(playerUUID);
		if(user.hasPermission("islandfly.bypass")) return;
		disableFly(user);
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		//If a player respawns on an island that he's added to, do nothing. 
		//Otherwise - disable Fly
		final Player player = event.getPlayer();
		final UUID playerUUID = player.getUniqueId();
		Optional<Island> island = plugin.getIslands().getIslandAt(player.getLocation());
		if(island.isPresent())
		if(island.get().getMembers().containsKey(playerUUID)) {
			//Enable only if it was previously enabled too
			if(player.getAllowFlight())
			player.setFlying(true);
		}
	}
	
	
	private void disableFly(final User user){
		final Player player = user.getPlayer();
		player.setFlying(false);
    }

}
