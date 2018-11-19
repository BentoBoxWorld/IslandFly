package bentobox.addon.islandfly;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.events.island.IslandEvent;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.managers.IslandsManager;

import java.util.UUID;

public class FlyListener implements Listener {

    private final FlySettings settings;
    private final BentoBox plugin;

    public FlyListener(final IslandFlyAddon addon){
        this.settings = addon.getSettings();
        this.plugin = addon.getPlugin();
    }

    @EventHandler(ignoreCancelled = true)
    public void onExitIsland(IslandEvent.IslandExitEvent event){
        final UUID playerUUID = event.getPlayerUUID();
        // Check only when player exit is own island
        if(!event.getIsland().getMembers().containsKey(playerUUID)) return;

        final User user = User.getInstance(playerUUID);
        final Player player = user.getPlayer();
        // Player is allowed to fly
        if(!player.getAllowFlight()) return;
        // Check bypass permission
        if(user.hasPermission("islandfly.bypass")) return;
        // Alert player fly will be disabled
        user.sendMessage("islandfly.fly-outside-alert");
        // Disable fly with a delay
        this.plugin.getServer().getScheduler().runTaskLater(plugin, ()->{
            // Check player not disconnected
            if(!user.isOnline()) return;
            final IslandsManager islands = plugin.getIslands();
            // Check player is not on his own island
            if(!(islands.userIsOnIsland(user.getWorld(), user)
            && islands.getIslandAt(user.getLocation()).get().getMembers().containsKey(playerUUID))){
                // Disable player fly and alert it
                player.setFlying(false);
                player.setAllowFlight(false);
                user.sendMessage("islandfly.disable-fly");
            }
        }, 20L* settings.getFlyTimeout());
    }

}
