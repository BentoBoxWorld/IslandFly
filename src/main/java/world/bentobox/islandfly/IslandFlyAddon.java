package world.bentobox.islandfly;

import org.bukkit.Material;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.managers.RanksManager;
import world.bentobox.islandfly.config.Settings;
import world.bentobox.islandfly.listeners.*;
import world.bentobox.level.Level;


/**
 * IslandFlyAddon main class. Enables addon.
 */
public class IslandFlyAddon extends Addon {
    /**
     * Settings object for IslandFlyAddon
     */
    private Settings settings;

    /**
     * Level addon instance.
     */
    private Level levelAddon;

    /**
     * A flag to allow or disallow flight on island
     * based on player's rank
     */
    public static final Flag ISLAND_FLY_PROTECTION =
            new Flag.Builder("ISLAND_FLY_PROTECTION", Material.ELYTRA)
            .type(Flag.Type.PROTECTION)
            .mode(Flag.Mode.ADVANCED)
            .defaultRank(RanksManager.MEMBER_RANK)
            .defaultSetting(true).build();

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
        this.settings = new Config<>(this, Settings.class).loadConfigObject();
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
            log("IslandFly addon reloaded.");
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
                getPlugin().log("Hooking into " + gameModeAddon.getDescription().getName());

                gameModeAddon.getPlayerCommand().ifPresent(
                        playerCommand -> {
                            new FlyToggleCommand(playerCommand, this);
                            hooked = true;
                        });

                ISLAND_FLY_PROTECTION.addGameModeAddon(gameModeAddon);
            }
        });

        if (hooked) {

            // Register Listeners
            registerListener(new FlyListener(this));
            registerListener(new FlyDeathListener(this));
            registerListener(new FlyLogoutListener(this));
            registerListener(new FlyLoginListener(this));
            registerListener(new FlyFlagListener(this));

            // Register a flag
            registerFlag(ISLAND_FLY_PROTECTION);
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
     * Check addon hooks.
     */
    public void allLoaded()
    {
        // Try to find Level addon and if it does not exist, display a warning
        this.getAddonByName("Level").ifPresentOrElse(addon ->
        {
            this.levelAddon = (Level) addon;
            this.log("Level Addon hooked into Level addon.");
        }, () ->
        {
            this.levelAddon = null;
        });
    }

    /**
     * This method loads addon configuration settings in memory.
     */
    private void loadSettings() {

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


    /**
     * Gets level addon.
     *
     * @return the level addon
     */
    public Level getLevelAddon()
    {
        return levelAddon;
    }
}
