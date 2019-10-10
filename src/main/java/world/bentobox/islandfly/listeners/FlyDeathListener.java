package world.bentobox.islandfly.listeners;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.islandfly.IslandFlyAddon;


/**
 * This class manages Death and Respawn options.
 */
public class FlyDeathListener implements Listener {

	/**
	 * BentoBox plugin instance.
	 */
    private final BentoBox plugin;


	/**
	 * Default constructor.
	 * @param addon IslandFlyAddon instance
	 */
	public FlyDeathListener(final IslandFlyAddon addon){
        this.plugin = addon.getPlugin();
    }


	/**
	 * Fired when player died. Removes fly ability in user world, if user does not have flybypass
	 * permission.
	 * @param event Instance of PlayerDeathEvent
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onDeath(final PlayerDeathEvent event) {
	    //Disable fly on death anyway
	    final User user = User.getInstance(event.getEntity().getUniqueId());

	    if (plugin.getIWM().getAddon(user.getWorld()).
			map(a -> user.hasPermission(a.getPermissionPrefix() + "island.flybypass")).
			orElse(false)) {
	    	return;
		}

	    disableFly(user);
	}


	/**
	 * Enable fly mode if player had it before.
	 * @param event Instance of PlayerRespawnEvent
	 */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onRespawn(PlayerRespawnEvent event) {
		
	    //If a player respawns on an island that he's added to, do nothing. 
	    //Otherwise - disable Fly
	    final Player player = event.getPlayer();
	    final UUID playerUUID = player.getUniqueId();
	    Optional<Island> island = plugin.getIslands().getIslandAt(player.getLocation());

	    if (island.isPresent() &&
			island.get().getMembers().containsKey(playerUUID) &&
			player.getAllowFlight()) {
	        //Enable only if it was previously enabled too
	       player.setFlying(true);
	    }
	}


	/**
	 * This method disables fly mode for given User.
	 * @param user Which must lose its fly ability.
	 */
	private void disableFly(final User user) {
		user.getPlayer().setFlying(false);
    }
}
