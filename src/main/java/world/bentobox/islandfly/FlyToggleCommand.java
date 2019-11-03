package world.bentobox.islandfly;

import org.bukkit.entity.Player;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.IslandsManager;
import world.bentobox.bentobox.util.Util;

import java.util.List;
import java.util.Optional;


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
    public boolean execute(User user, String label, List<String> args) {

        // Checks world from corresponding gamemode command with the world player is executing in
        if (this.getWorld() != Util.getWorld(user.getWorld())) {
            user.sendMessage("islandfly.wrong-world");
            return false;
        }

        // Allow this command only on island
        final IslandsManager islands = this.getIslands();
        islands.userIsOnIsland(user.getWorld(), user);
        final Optional<Island> island = islands.getIslandAt(user.getLocation());

        if (!user.hasPermission(this.getPermissionPrefix() + "island.flybypass")
                && (!island.isPresent() || !island.get().getMembers().containsKey(user.getUniqueId()))) {
            user.sendMessage("islandfly.command.only-on-island");
            return false;
        }

        final Player player = user.getPlayer();

        if (user.getPlayer().getAllowFlight()) {

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
