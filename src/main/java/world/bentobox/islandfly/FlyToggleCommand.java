package world.bentobox.islandfly;

import java.util.List;

import org.bukkit.entity.Player;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.util.Util;


/**
 * This command allows to enable and disable fly mode.
 */
public class FlyToggleCommand extends CompositeCommand {
    /**
     * Default constructor
     * @param parent Instance of CompositeCommand
     */
    public FlyToggleCommand(CompositeCommand parent) {
        super(parent, "fly");
    }


    @Override
    public void setup() {
        this.setPermission("island.fly");
        this.setOnlyPlayer(true);
        this.setDescription("islandfly.command.description");
    }

    @Override
    public boolean canExecute(User user, String label, List<String> args) {

        // Checks world from corresponding gamemode command with the world player is executing in
        if (this.getWorld() != Util.getWorld(user.getWorld())) {
            user.sendMessage("islandfly.wrong-world");
            return false;
        }

        Island island = getIslands().getIslandAt(user.getLocation()).orElse(null);

        if (island == null) return false;

        // Gets the island at User's location
        // If, statement above did return true, there is no need to check #isPresent

        // Enable fly if island is a spawn and user has permission for it
        if (island.isSpawn()) {
            if (user.hasPermission(this.getPermissionPrefix() + "island.flyspawn"))
                return true;
        }

        if (!island.isAllowed(user, IslandFlyAddon.ISLAND_FLY_PROTECTION) && !user.hasPermission(this.getPermissionPrefix() + "island.flybypass")) {

            user.sendMessage("islandfly.command.not-allowed-fly");
            return false;
        }


        return true;
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        final Player player = user.getPlayer();

        if (player.getAllowFlight()) {

            // Disable fly and notify player
            player.setFlying(false);
            player.setAllowFlight(false);
            user.sendMessage("islandfly.disable-fly");
        } else {

            // Enable fly and notify player
            player.setAllowFlight(true);
            user.sendMessage("islandfly.enable-fly");
        }
        return true;
    }
}
