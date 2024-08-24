//
// Created by BONNe
// Copyright - 2022
//


package world.bentobox.islandfly;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.Pladdon;



public class IslandFlyPladdon extends Pladdon
{
    Addon addon;
    @Override
    public Addon getAddon()
    {
        if (addon == null) {
            addon = new IslandFlyAddon();
        }
        return addon;
    }
}
