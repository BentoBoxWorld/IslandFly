package world.bentobox.islandfly.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import world.bentobox.bentobox.api.events.island.IslandEnterEvent;
import world.bentobox.bentobox.api.events.island.IslandExitEvent;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.islandfly.IslandFlyAddon;

/**
 * This class manages players fly ability.
 */
public class FlyListener implements Listener {

    /**
     * Addon instance object.
     */
    private final IslandFlyAddon addon;


    /**
     * Default constructor.
     * @param addon instance of IslandFlyAddon
     */
    public FlyListener(final IslandFlyAddon addon) {
        this.addon = addon;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onToggleFlight(final PlayerToggleFlightEvent event) {
        final User user = User.getInstance(event.getPlayer());
        if (checkUser(user)) {
            user.sendMessage("islandfly.not-allowed");
        }
    }

    /**
     * @param user user
     * @return true if fly was blocked
     */
    private boolean checkUser(User user) {
        String permPrefix = addon.getPlugin().getIWM().getPermissionPrefix(user.getWorld());
        // Ignore ops
        if (user.isOp() || user.getPlayer().getGameMode().equals(GameMode.CREATIVE)
                || user.getPlayer().getGameMode().equals(GameMode.SPECTATOR)
                || user.hasPermission(permPrefix + "island.flybypass")) return false;
        return removeFly(user);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEnterIsland(final IslandEnterEvent event) {
        final User user = User.getInstance(event.getPlayerUUID());
        // Wait until after arriving at the island
        Bukkit.getScheduler().runTask(this.addon.getPlugin(), () -> checkUser(user));
    }

    /**
     * This method is triggered when player leaves their island.
     * @param event instance of IslandEvent.IslandExitEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onExitIsland(final IslandExitEvent event) {

        final User user = User.getInstance(event.getPlayerUUID());
        String permPrefix = addon.getPlugin().getIWM().getPermissionPrefix(user.getWorld());
        // Ignore ops
        if (user.isOp() || user.getPlayer().getGameMode().equals(GameMode.CREATIVE)
                || user.getPlayer().getGameMode().equals(GameMode.SPECTATOR)
                || user.hasPermission(permPrefix + "island.flybypass")
                || (!user.hasPermission(permPrefix + "island.fly")
                        && !user.hasPermission(permPrefix + "island.flyspawn"))) return;
        // Alert player fly will be disabled
        final int flyTimeout = this.addon.getSettings().getFlyTimeout();

        // If timeout is 0 or less disable fly immediately
        if (flyTimeout <= 0) {
            removeFly(user);
            return;
        }

        // Else disable fly with a delay
        if (user.getPlayer().isFlying()) {
            user.sendMessage("islandfly.fly-outside-alert", TextVariables.NUMBER, String.valueOf(flyTimeout));
        }

        Bukkit.getScheduler().runTaskLater(this.addon.getPlugin(), () -> removeFly(user), 20L* flyTimeout);
    }


    /**
     * Remove fly from a player if required
     * @param user - user to check
     * @return true if fly is removed, otherwise false
     */
    boolean removeFly(User user) {
        // Verify player is still online
        if (!user.isOnline()) return false;

        Island is = addon.getIslands().getProtectedIslandAt(user.getLocation()).orElse(null);

        if (is == null) {
            disableFly(user);
            return true;
        }

        // Check if player is back on a spawn island
        if (is.isSpawn()) {
            if (this.addon.getPlugin().getIWM().getAddon(user.getWorld())
                    .map(a -> !user.hasPermission(a.getPermissionPrefix() + "island.flyspawn")).orElse(false)) {

                disableFly(user);
                return true;
            }
            return false;
        }

        // Check if player is allowed to fly on the island he is at that moment
        if (!is.isAllowed(user, IslandFlyAddon.ISLAND_FLY_PROTECTION)) {
            disableFly(user);
            return true;
        }
        return false;
    }



    /**
     * Disable player fly and alert it
     * @param user - user to disable
     */
    private void disableFly(final User user) {

        final Player player = user.getPlayer();
        if (player.isFlying())
            user.sendMessage("islandfly.disable-fly");

        player.setFlying(false);
        player.setAllowFlight(false);
    }
}
