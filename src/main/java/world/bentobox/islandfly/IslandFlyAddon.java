package world.bentobox.islandfly;

import world.bentobox.islandfly.listeners.FlyDeathListener;
import world.bentobox.islandfly.listeners.FlyListener;
import world.bentobox.islandfly.listeners.FlyLogoutListener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import world.bentobox.bentobox.api.addons.Addon;

public class IslandFlyAddon extends Addon {

    private FlySettings settings;
    private boolean hooked=false;
    
    
    @Override
    public void onEnable() {
        // Load configuration
        this.settings = new FlySettings(this);
          
        //Hook into gamemodes
        this.getPlugin().getAddonsManager().getGameModeAddons().forEach(gm -> {
            if (gm.getPlayerCommand().isPresent()) {
        	    new FlyToggleCommand(gm.getPlayerCommand().get());
        	    hooked=true;
        	}
        	
        });
        
        if (hooked) {	
            
            // Register Listeners
            final PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvents(new FlyListener(this), this.getPlugin());
            pluginManager.registerEvents(new FlyDeathListener(this), this.getPlugin());
            
            // Register listener to disable fly on logout if activated
            if (settings.isFlyDisabledOnLogout()) {
                pluginManager.registerEvents(new FlyLogoutListener(), this.getPlugin());
            }
        }
    }

    
    @Override
    public void onDisable() {
        //Nothing to do here
    }

    
    /**
     * Get addon settings
     * @return settings
     */
    public FlySettings getSettings() {
        return settings;
    }

}
