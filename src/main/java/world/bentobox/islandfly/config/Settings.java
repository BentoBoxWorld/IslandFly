//
// Created by BONNe
// Copyright - 2019
//


package world.bentobox.islandfly.config;


import java.util.HashSet;
import java.util.Set;

import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;


/**
 * Settings that implements ConfigObject is powerful and dynamic Config Objects that
 * does not need custom parsing. If it is correctly loaded, all its values will be available.
 *
 * Without Getter and Setter this class will not work.
 *
 * To specify location for config object to be stored, you should use @StoreAt(filename="{config file name}", path="{Path to your addon}")
 * To save comments in config file you should use @ConfigComment("{message}") that adds any message you want to be in file.
 */
@StoreAt(filename="config.yml", path="addons/IslandFly")
@ConfigComment("IslandFlyAddon Configuration [version]")
@ConfigComment("This config file is dynamic and saved when the server is shutdown.")
public class Settings implements ConfigObject
{
    // ---------------------------------------------------------------------
    // Section: Getters and Setters
    // ---------------------------------------------------------------------


    /**
     * Method Settings#getFlyTimeout returns the flyTimeout of this object.
     *
     * @return the flyTimeout (type int) of this object.
     */
    public int getFlyTimeout()
    {
        return flyTimeout;
    }


    /**
     * Method Settings#setFlyTimeout sets new value for the flyTimeout of this object.
     * @param flyTimeout new value for this object.
     *
     */
    public void setFlyTimeout(int flyTimeout)
    {
        this.flyTimeout = flyTimeout;
    }


    /**
     * Method Settings#isFlyDisableOnLogout returns the flyDisableOnLogout of this object.
     *
     * @return the flyDisableOnLogout (type boolean) of this object.
     */
    public boolean isFlyDisableOnLogout()
    {
        return flyDisableOnLogout;
    }


    /**
     * Method Settings#setFlyDisableOnLogout sets new value for the flyDisableOnLogout of this object.
     * @param flyDisableOnLogout new value for this object.
     *
     */
    public void setFlyDisableOnLogout(boolean flyDisableOnLogout)
    {
        this.flyDisableOnLogout = flyDisableOnLogout;
    }


    /**
     * This method returns the disabledGameModes value.
     *
     * @return the value of disabledGameModes.
     */
    public Set<String> getDisabledGameModes()
    {
        return disabledGameModes;
    }


    /**
     * This method sets the disabledGameModes value.
     *
     * @param disabledGameModes the disabledGameModes new value.
     */
    public void setDisabledGameModes(Set<String> disabledGameModes)
    {
        this.disabledGameModes = disabledGameModes;
    }
    
    
    
    
    /**
     * Method Settings#isFlyDisableOnLogout returns the flyDisableOnLogout of this object.
     *
     * @return the flyDisableOnLogout (type boolean) of this object.
     */
    public boolean isAllowCommandOutsideProtectionRange()
    {
        return allowCommandOutsideProtectionRange;
    }


    /**
     * Method Settings#setFlyDisableOnLogout sets new value for the flyDisableOnLogout of this object.
     * @param flyDisableOnLogout new value for this object.
     *
     */
    public void setAllowCommandOutsideProtectionRange(boolean commandAllowed)
    {
        this.allowCommandOutsideProtectionRange = commandAllowed;
    }


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------

    @ConfigComment("")
    @ConfigComment("This allows to define timeout in seconds on which fly mode will be disabled")
    @ConfigComment("when player leaves its island. Zero or negative number will result in immediate")
    @ConfigComment("fly mode disabling. Players with the bskyblock.island.flybypass permission (or similar)")
    @ConfigComment("can fly outside the island boundary.")
    @ConfigEntry(path = "fly-timeout")
    private int flyTimeout = 5;

    @ConfigComment("")
    @ConfigComment("This allows to change if players should lose their fly mode if they quit server.")
    @ConfigEntry(path = "logout-disable-fly")
    private boolean flyDisableOnLogout = false;

    @ConfigComment("")
    @ConfigComment("This list stores GameModes in which islandFly addon should not work.")
    @ConfigComment("To disable addon it is necessary to write its name in new line that starts with -. Example:")
    @ConfigComment("disabled-gamemodes:")
    @ConfigComment(" - BSkyBlock")
    @ConfigEntry(path = "disabled-gamemodes")
    private Set<String> disabledGameModes = new HashSet<>();
    
    @ConfigComment("")
    @ConfigComment("This allows the player to use the command outside the island protection range.")
    @ConfigEntry(path = "allow-command-outside-protection-range")
    private boolean allowCommandOutsideProtectionRange = false;
}
