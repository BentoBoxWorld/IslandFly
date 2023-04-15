//
// Created by BONNe
// Copyright - 2022
//


package world.bentobox.islandfly;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.Pladdon;



public class IslandFlyPladdon extends Pladdon
{
    @Override
    public Addon getAddon()
    {
        return new IslandFlyAddon();
    }
}
