package world.bentobox.islandfly.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import world.bentobox.bentobox.api.events.flags.FlagProtectionChangeEvent;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.islandfly.IslandFlyAddon;

public class FlyFlagListener implements Listener {

    private IslandFlyAddon addon;

    public FlyFlagListener(IslandFlyAddon addon) {
        this.addon = addon;
    }

    @EventHandler
    public void onFlagChange(FlagProtectionChangeEvent e) {

        if (!e.getEditedFlag().equals(IslandFlyAddon.ISLAND_FLY_PROTECTION))
            return;

        Island island = e.getIsland();

        // Stream through all of the flying and not allowed users at
        // the moment and warn them that their fly is about to turn off
        e.getIsland().getPlayersOnIsland().parallelStream()
        .filter(Player::isFlying)
        .filter(p -> !(island.isAllowed(User.getInstance(p), IslandFlyAddon.ISLAND_FLY_PROTECTION) || p.isOp()))
        .forEach(p -> {

            startDisabling(p, island);
        });
    }

    public void startDisabling(Player p, Island island) {

        int flyTimeout = this.addon.getSettings().getFlyTimeout();
        User user = User.getInstance(p);


        // Alert player fly will be disabled
        user.sendMessage("islandfly.fly-turning-off-alert", TextVariables.NUMBER, String.valueOf(flyTimeout));

        // If timeout is 0 or less disable fly immediately
        if (flyTimeout <= 0) {

            p.setFlying(false);
            p.setAllowFlight(false);
            user.sendMessage("islandfly.disable-fly");

            return;
        }

        // Else disable fly with a delay
        addon.getServer().getScheduler().runTaskLater(this.addon.getPlugin(), () -> {

            // Verify that player is still online
            if (!user.isOnline()) return;

            // Check if user was reallowed to fly in the meantime
            if (!island.isAllowed(user,IslandFlyAddon.ISLAND_FLY_PROTECTION)) {

                // Silent cancel fly if player changed island in the meantime
                // It will be the job of Enter/Exit island event to turn fly off if required
                if (!island.onIsland(p.getLocation()))
                    return;

                p.setFlying(false);
                p.setAllowFlight(false);
                user.sendMessage("islandfly.disable-fly");
            }
            else {
                user.sendMessage("islandfly.reallowed-fly");
            }

        }, 20L* flyTimeout);
    }
}