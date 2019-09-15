package world.bentobox.islandfly;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.islandfly.listeners.FlyDeathListener;
import world.bentobox.islandfly.listeners.FlyListener;
import world.bentobox.islandfly.listeners.FlyLogoutListener;

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
            registerListener(new FlyListener(this));
            registerListener(new FlyDeathListener(this));

            // Register listener to disable fly on logout if activated
            if (settings.isFlyDisabledOnLogout()) {
                registerListener(new FlyLogoutListener());
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
