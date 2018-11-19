package bentobox.addon.islandfly;

public class FlySettings {

    private int flyTimeout;

    public FlySettings(final IslandFlyAddon addon){
        // Load configuration file
        addon.saveDefaultConfig();
        // Load datas from config
        this.flyTimeout = addon.getConfig().getInt("fly-timeout", 5);
    }

    /**
     * get amount of time in seconds player can fly outside is island
     * @return number of seconds
     */
    public int getFlyTimeout() {
        return flyTimeout;
    }
}
