//
// Created by BONNe
// Copyright - 2022
//


package world.bentobox.islandfly;


import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.Pladdon;


/**
 * @author BONNe
 */
@Plugin(name="IslandFly", version="1.0")
@ApiVersion(ApiVersion.Target.v1_17)
public class IslandFlyPladdon extends Pladdon
{
    @Override
    public Addon getAddon()
    {
        return new IslandFlyAddon();
    }
}
