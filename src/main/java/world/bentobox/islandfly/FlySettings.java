package world.bentobox.islandfly;

public class FlySettings {

    private int flyTimeout;
    private boolean flyDisabledOnLogout;

    public FlySettings(final IslandFlyAddon addon) {
        
        // Load configuration file
        addon.saveDefaultConfig();
        // Load datas from config
        this.flyTimeout = addon.getConfig().getInt("fly-timeout", 5);
        this.flyDisabledOnLogout = addon.getConfig().getBoolean("logout-disable-fly", false);
    }

    /**
     * get amount of time in seconds player can fly outside is island
     * @return number of seconds
     */
    public int getFlyTimeout() {
        return flyTimeout;
    }

    /**
     * Plugin must disable fly on logout or not
     * @return boolean
     */
    public boolean isFlyDisabledOnLogout() {
        return flyDisabledOnLogout;
    }
    
}
