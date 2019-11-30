package world.bentobox.islandfly.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import world.bentobox.bentobox.api.events.island.IslandEvent;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.islandfly.IslandFlyAddon;

import java.util.Optional;


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

        final User user = User.getInstance(event.getPlayerUUID());

        final Island i = addon.getIslands().getIslandAt(user.getLocation()).orElse(null);

        if (i == null) return;

        if (!user.getPlayer().getAllowFlight()) return;

        // Bypass permission
        if (this.addon.getPlugin().getIWM().getAddon(user.getWorld())
                .map(a -> user.hasPermission(a.getPermissionPrefix() + "island.flybypass")).orElse(false)) {
            return;
        }

        // Alert player fly will be disabled
        final int flyTimeout = this.addon.getSettings().getFlyTimeout();

        // If timeout is 0 or less disable fly immediately
        if (flyTimeout <= 0) {
            disableFly(user);
            return;
        }

        // If this is not true, player's fly will silently be turned off
        if (user.getPlayer().isFlying())
        user.sendMessage("islandfly.fly-outside-alert", TextVariables.NUMBER, String.valueOf(flyTimeout));

        // Else disable fly with a delay
        this.addon.getServer().getScheduler().runTaskLater(this.addon.getPlugin(), () -> {

            // Verify player is still online
            if (!user.isOnline()) return;

            Island is = addon.getIslands().getIslandAt(user.getLocation()).orElse(null);

            if (is == null) return;

            // Check if player is back on a spawn island
            if (is.isSpawn()) {
                if (this.addon.getPlugin().getIWM().getAddon(user.getWorld())
                        .map(a -> !user.hasPermission(a.getPermissionPrefix() + "island.flyspawn")).orElse(false)) {

                    disableFly(user);
                    return;
                }
                if (user.getPlayer().isFlying())
                    user.sendMessage("islandfly.cancel-disable");
                return;
            }

            // Check if player was reallowed to fly on the island he is at that moment
            if (!is.isAllowed(user, IslandFlyAddon.ISLAND_FLY_PROTECTION) || !is.onIsland(user.getLocation())) {
                disableFly(user);
                return;
            }

            // If false, will stay silent
            if (user.getPlayer().isFlying())
                user.sendMessage("islandfly.cancel-disable");
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
