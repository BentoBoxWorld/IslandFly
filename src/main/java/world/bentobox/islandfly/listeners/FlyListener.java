package world.bentobox.islandfly.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import world.bentobox.bentobox.api.events.island.IslandEvent;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.managers.IslandsManager;
import world.bentobox.bentobox.util.Util;
import world.bentobox.islandfly.IslandFlyAddon;


/**
 * This class manages players fly ability.
 */
public class FlyListener implements Listener {

    /**
     * Addon instance object.
     */
    private IslandFlyAddon addon;


    /**
     * Default constructor.
     * @param addon instance of IslandFlyAddon
     */
    public FlyListener(final IslandFlyAddon addon) {
        this.addon = addon;
    }


    /**
     * This method is triggered when player leaves its island.
     * @param event instance of IslandEvent.IslandExitEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onExitIsland(final IslandEvent.IslandExitEvent event) {
        // Check only when player exit is own island
        // Player already flying
        final User user = User.getInstance(event.getPlayerUUID());

        if (!addon.getIslands().userIsOnIsland(Util.getWorld(event.getLocation().getWorld()), user)) {
            return;
        }
        
        if (!user.getPlayer().getAllowFlight()) {
            return;
        }

        // Bypass permission
        if (this.addon.getPlugin().getIWM().getAddon(user.getWorld())
                .map(a -> user.hasPermission(a.getPermissionPrefix() + "island.flybypass")).orElse(false)) {
            return;
        }

        // Alert player fly will be disabled
        final int flyTimeout = this.addon.getSettings().getFlyTimeout();

        user.sendMessage("islandfly.fly-outside-alert", TextVariables.NUMBER, String.valueOf(flyTimeout));
        // If timeout is 0 or less disable fly immediately
        if (flyTimeout <= 0) {
            disableFly(user);
            return;
        }
        // Else disable fly with a delay
        this.addon.getServer().getScheduler().runTaskLater(this.addon.getPlugin(), () -> {

            // Verify player is still online
            if (!user.isOnline()) return;

            final IslandsManager islands = this.addon.getIslands();

            // Check player is not on his own island
            if (!(islands.userIsOnIsland(Util.getWorld(user.getWorld()), user))) {
                disableFly(user);
            }
            else {
                user.sendMessage("islandfly.cancel-disable");
            }
        }, 20L* flyTimeout);
    }


    /**
     * Disable player fly and alert it
     * @param user
     */
    private void disableFly(final User user) {

        final Player player = user.getPlayer();

        player.setFlying(false);
        player.setAllowFlight(false);
        user.sendMessage("islandfly.disable-fly");
    }
}
