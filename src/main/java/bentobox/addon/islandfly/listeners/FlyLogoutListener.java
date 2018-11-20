package bentobox.addon.islandfly.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class FlyLogoutListener implements Listener {

    @EventHandler
    public void onLogout(final PlayerQuitEvent event){

        final Player player = event.getPlayer();

        if(!event.getPlayer().getAllowFlight()) return;
        if(event.getPlayer().hasPermission("islandfly.bypass")) return;

        // Disable fly
        player.setFlying(false);
        player.setAllowFlight(false);
    }

}
