package bentobox.addon.islandfly;

import org.bukkit.entity.Player;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;

import java.util.List;

public class FlyToggleCommand extends CompositeCommand {

    public FlyToggleCommand(final CompositeCommand baseCmd) {
        super(baseCmd, "fly");
    }

    @Override
    public void setup() {
        this.setPermission("fly");
        this.setOnlyPlayer(true);
        this.setDescription("islandfly.command.description");
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        final Player player = user.getPlayer();
        if(user.getPlayer().getAllowFlight()){
            // Disable fly and notify player
            player.setFlying(false);
            player.setAllowFlight(false);
            user.sendMessage("islandfly.disable-fly");
        }else{
            // Enable fly and notify player
            player.setAllowFlight(true);
            user.sendMessage("islandfly.enable-fly");
        }
        return true;
    }
}
