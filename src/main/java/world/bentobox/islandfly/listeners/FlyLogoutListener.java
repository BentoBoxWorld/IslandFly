package world.bentobox.islandfly.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import world.bentobox.islandfly.IslandFlyAddon;


/**
 * This class disables fly mode if player quits server.
 */
public class FlyLogoutListener implements Listener {

    /**
     * IslandFlyAddon instance.
     */
    private final IslandFlyAddon addon;


    /**
     * Default constructor.
     * @param addon instance of IslandFlyAddon
     */
    public FlyLogoutListener(IslandFlyAddon addon)
    {
        this.addon = addon;
    }


    /**
     * Disable player fly mode on logout
     * @param event Instance of PlayerQuitEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLogout(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (player.getAllowFlight() && this.addon.getSettings().isFlyDisableOnLogout())
        {
            // Disable fly
            player.setFlying(false);
            player.setAllowFlight(false);
        }
    }
}
