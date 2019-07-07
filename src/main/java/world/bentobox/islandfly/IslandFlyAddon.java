package world.bentobox.islandfly;

import world.bentobox.islandfly.listeners.FlyDeathListener;
import world.bentobox.islandfly.listeners.FlyListener;
import world.bentobox.islandfly.listeners.FlyLogoutListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.commands.CompositeCommand;

public class IslandFlyAddon extends Addon {

    private FlySettings settings;

    @Override
    public void onEnable() {
        // Load configuration
        this.settings = new FlySettings(this);
        // Register Listeners
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new FlyListener(this), this.getPlugin());
        pluginManager.registerEvents(new FlyDeathListener(this), this.getPlugin());
        // Register listener to disable fly on logout if activated
        if(settings.isFlyDisabledOnLogout())
            pluginManager.registerEvents(new FlyLogoutListener(), this.getPlugin());
        // BSkyBlock hook in
        this.getPlugin().getAddonsManager().getAddonByName("BSkyBlock").ifPresent(bskyblock -> {
            final CompositeCommand bsbIslandCmd = BentoBox.getInstance().getCommandsManager().getCommand("island");
            if (bsbIslandCmd != null) {
                new FlyToggleCommand(bsbIslandCmd);
            }
        });
        // AcidIsland hook in
        this.getPlugin().getAddonsManager().getAddonByName("AcidIsland").ifPresent(acidIsland -> {
            final CompositeCommand acidIslandCmd = getPlugin().getCommandsManager().getCommand("ai");
            if (acidIslandCmd != null) {
                new FlyToggleCommand(acidIslandCmd);
            }
        });
    }

    @Override
    public void onDisable() {}

    /**
     * Get addon settings
     * @return settings
     */
    public FlySettings getSettings() {
        return settings;
    }

}
