package world.bentobox.islandfly;

import java.util.List;

import org.bukkit.entity.Player;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.util.Util;
import world.bentobox.islandfly.config.Settings;


/**
 * This command allows to enable and disable fly mode.
 */
public class FlyToggleCommand extends CompositeCommand {


    private final Settings settings;
    private final IslandFlyAddon islandFlyAddon;

    /**
     * Default constructor
     * @param parent Instance of CompositeCommand
     */
    public FlyToggleCommand(CompositeCommand parent, IslandFlyAddon addon) {
        super(parent, "fly");
        this.settings = addon.getSettings();
        this.islandFlyAddon = addon;
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

        // Enable fly if island is a spawn and user has permission for it
        if (island.isSpawn() && user.hasPermission(this.getPermissionPrefix() + "island.flyspawn")) {
            return true;
        }

        if (!island.isAllowed(user, IslandFlyAddon.ISLAND_FLY_PROTECTION) && !user.hasPermission(this.getPermissionPrefix() + "island.flybypass")) {

            user.sendMessage("islandfly.command.not-allowed-fly");
            return false;
        }


        if (!this.settings.isAllowCommandOutsideProtectionRange()
                && !island.getProtectionBoundingBox().contains(user.getLocation().toVector())) {

            user.sendMessage("islandfly.outside-protection-range");
            return false;

        }

        if(islandFlyAddon.getSettings().getFlyMinLevel() > 1 && islandFlyAddon.getLevelAddon() != null) {
            if (islandFlyAddon.getLevelAddon().getIslandLevel(island.getWorld(), island.getOwner()) < islandFlyAddon.getSettings().getFlyMinLevel()) {
                user.sendMessage("islandfly.fly-min-level-alert", TextVariables.NUMBER, String.valueOf(islandFlyAddon.getSettings().getFlyMinLevel()));
                return false;
            }
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
