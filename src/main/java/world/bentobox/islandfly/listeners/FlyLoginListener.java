package world.bentobox.islandfly.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.islandfly.IslandFlyAddon;

/**
 * This class disables fly mode if player quits server.
 */
public class FlyLoginListener implements Listener {

    /**
     * IslandFlyAddon instance.
     */
    private final IslandFlyAddon islandFlyAddon;


    /**
     * Default constructor.
     * @param islandFlyAddon instance of IslandFlyAddon
     */
    public FlyLoginListener(IslandFlyAddon islandFlyAddon)
    {
        this.islandFlyAddon = islandFlyAddon;
    }


    /**
     * Disable player fly mode on logout
     * @param event Instance of PlayerQuitEvent
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLogin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final User user = User.getInstance(player);
        final String permPrefix = islandFlyAddon.getPlugin().getIWM().getPermissionPrefix(player.getWorld());
        if (player.hasPermission(permPrefix + "island.fly")
                && !this.islandFlyAddon.getSettings().isFlyDisableOnLogout()
                && isInAir(player)
                && islandFlyAddon.getIslands().userIsOnIsland(user.getWorld(), user)
                && !islandFlyAddon.getIslands().getIslandAt(user.getLocation()).map(i -> {

                    if(islandFlyAddon.getSettings().getFlyMinLevel() > 1 && islandFlyAddon.getLevelAddon() != null) {
                        if (islandFlyAddon.getLevelAddon().getIslandLevel(i.getWorld(), i.getOwner()) < islandFlyAddon.getSettings().getFlyMinLevel()) {
                            user.sendMessage("islandfly.fly-min-level-alert", TextVariables.NUMBER, String.valueOf(islandFlyAddon.getSettings().getFlyMinLevel()));
                            return false;
                        }
                    }

                    if (i.isAllowed(user, IslandFlyAddon.ISLAND_FLY_PROTECTION)) {
                        // Enable fly
                        player.setFallDistance(0);
                        player.setAllowFlight(true);
                        player.setFlying(true);
                        user.sendMessage("islandfly.enable-fly");
                        return true;
                    }
                    return false;
                }).orElse(false)) {
            user.sendMessage("islandfly.command.not-allowed-fly");
        }
    }


    private boolean isInAir(Player player) {
        Block b = player.getLocation().getBlock();
        return player.getLocation().getBlockY() > (player.getWorld().getMinHeight() + 1) && b.getRelative(BlockFace.DOWN).isEmpty() && b.getRelative(BlockFace.DOWN, 2).isEmpty();
    }
}
