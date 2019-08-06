package world.bentobox.islandfly.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class FlyLogoutListener implements Listener {

	
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLogout(final PlayerQuitEvent event) {

        final Player player = event.getPlayer();
	
	//Stop further execution if fly wasn't toggled
        if (!player.getAllowFlight()) return;

        // Disable fly
        player.setFlying(false);
        player.setAllowFlight(false);
    }

}
