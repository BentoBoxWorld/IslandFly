package world.bentobox.islandfly;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.islandfly.config.Settings;
import world.bentobox.islandfly.listeners.FlyDeathListener;
import world.bentobox.islandfly.listeners.FlyListener;
import world.bentobox.islandfly.listeners.FlyLogoutListener;


/**
 * IslandFlyAddon main class. Enables addon.
 */
public class IslandFlyAddon extends Addon {
    /**
     * Settings object for IslandFlyAddon
     */
    private Settings settings;

    /**
     * Boolean that indicate if addon is hooked into any gamemode.
     */
    private boolean hooked;


    /**
     * Executes code when loading the addon. This is called before {@link #onEnable()}. This should preferably
     * be used to setup configuration and worlds.
     */
    @Override
    public void onLoad()
    {
        super.onLoad();
        // Save default config.yml
        this.saveDefaultConfig();
        // Load the plugin's config
        this.loadSettings();
    }


    /**
     * Executes code when reloading the addon.
     */
    @Override
    public void onReload()
    {
        super.onReload();

        if (this.hooked) {
            this.loadSettings();
            this.getLogger().info("IslandFly addon reloaded.");
        }
    }


    /**
     * Loads addon settings and hooks into available GameModes
     */
    @Override
    public void onEnable() {
        //Hook into gamemodes

        this.getPlugin().getAddonsManager().getGameModeAddons().forEach(gameModeAddon -> {
            if (!this.settings.getDisabledGameModes().contains(gameModeAddon.getDescription().getName()))
            {
                gameModeAddon.getPlayerCommand().ifPresent(
                    playerCommand -> {
                        new FlyToggleCommand(playerCommand);
                        hooked = true;
                    });
            }
        });

        if (hooked) {

            // Register Listeners
            registerListener(new FlyListener(this));
            registerListener(new FlyDeathListener(this));
            registerListener(new FlyLogoutListener(this));
        }
    }


    /**
     * Disable addon.
     */
    @Override
    public void onDisable() {
        //Nothing to do here
    }


    /**
     * This method loads addon configuration settings in memory.
     */
    private void loadSettings() {
        this.settings = new Config<>(this, Settings.class).loadConfigObject();

        if (this.settings == null) {
            // Disable
            this.logError("IslandFly settings could not load! Addon disabled.");
            this.setState(State.DISABLED);
        }
    }


    /**
     * Get addon settings
     * @return settings
     */
    public Settings getSettings() {
        return settings;
    }
}
