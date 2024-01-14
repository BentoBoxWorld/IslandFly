package world.bentobox.islandfly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.CommandsManager;
import world.bentobox.bentobox.managers.IslandsManager;
import world.bentobox.bentobox.util.Util;
import world.bentobox.islandfly.config.Settings;

/**
 * @author tastybento
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, BentoBox.class, Util.class})
public class FlyToggleCommandTest {

    @Mock
    private CompositeCommand ic;
    private UUID uuid;
    @Mock
    private User user;
    @Mock
    private IslandFlyAddon addon;
    @Mock
    private World world;
    @Mock
    private Player p;
    private FlyToggleCommand ftc;
    @Mock
    private IslandsManager im;
    @Mock
    private @Nullable Location location;
    @Mock
    private Island island;
    private Settings settings;
    @Mock
    private BoundingBox box;


    /**
     * @throws java.lang.Exception
     */

    @Before
    public void setUp() throws Exception {
        // Set up plugin
        BentoBox plugin = mock(BentoBox.class);
        Whitebox.setInternalState(BentoBox.class, "instance", plugin);
        User.setPlugin(plugin);

        // Command manager
        CommandsManager cm = mock(CommandsManager.class);
        when(plugin.getCommandsManager()).thenReturn(cm);
        // Addon
        when(ic.getAddon()).thenReturn(addon);
        when(ic.getPermissionPrefix()).thenReturn("bskyblock.");
        when(ic.getLabel()).thenReturn("island");
        when(ic.getTopLabel()).thenReturn("island");
        when(ic.getWorld()).thenReturn(world);
        when(ic.getTopLabel()).thenReturn("bsb");

        // World
        when(world.toString()).thenReturn("world");

        // Player
        // Sometimes use Mockito.withSettings().verboseLogging()
        when(user.isOp()).thenReturn(false);
        uuid = UUID.randomUUID();
        when(user.getUniqueId()).thenReturn(uuid);
        when(user.getPlayer()).thenReturn(p);
        when(user.getName()).thenReturn("tastybento");
        when(user.getPermissionValue(anyString(), anyInt())).thenReturn(-1);
        when(user.isPlayer()).thenReturn(true);
        when(user.getLocation()).thenReturn(location);

        // Util
        PowerMockito.mockStatic(Util.class);
        when(Util.getWorld(any())).thenReturn(world);

        // Island Manager
        when(plugin.getIslands()).thenReturn(im);
        Optional<Island> opIsland = Optional.of(island);
        when(im.getIslandAt(any())).thenReturn(opIsland);

        // Settings
        settings = new Settings();
        when(addon.getSettings()).thenReturn(settings);

        // Island
        when(island.getProtectionBoundingBox()).thenReturn(box);
        when(location.toVector()).thenReturn(new Vector(0,60,0));
        // Locations are always inside the box for now
        when(box.contains(any(Vector.class))).thenReturn(true);

        ftc = new FlyToggleCommand(ic, addon);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link world.bentobox.islandfly.FlyToggleCommand#FlyToggleCommand(world.bentobox.bentobox.api.commands.CompositeCommand)}.
     */
    @Test
    public void testFlyToggleCommand() {
        assertEquals("fly", ftc.getLabel());
    }

    /**
     * Test method for {@link world.bentobox.islandfly.FlyToggleCommand#setup()}.
     */
    @Test
    public void testSetup() {
        assertEquals("bskyblock.island.fly", ftc.getPermission());
        assertEquals("islandfly.command.description", ftc.getDescription());
        assertTrue(ftc.isOnlyPlayer());
    }

    /**
     * Test method for {@link world.bentobox.islandfly.FlyToggleCommand#canExecute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testCanExecuteWrongWorld() {
        when(Util.getWorld(any())).thenReturn(mock(World.class));
        assertFalse(ftc.canExecute(user, "fly", Collections.emptyList()));
        verify(user).sendMessage("islandfly.wrong-world");

    }
    /**
     * Test method for {@link world.bentobox.islandfly.FlyToggleCommand#canExecute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testCanExecuteNoIsland() {
        when(im.getIslandAt(any())).thenReturn(Optional.empty());
        assertFalse(ftc.canExecute(user, "fly", Collections.emptyList()));

    }
    /**
     * Test method for {@link world.bentobox.islandfly.FlyToggleCommand#canExecute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testCanExecuteSpawn() {
        when(island.isSpawn()).thenReturn(true);
        when(user.hasPermission(eq("bskyblock.island.flyspawn"))).thenReturn(true);
        assertTrue(ftc.canExecute(user, "fly", Collections.emptyList()));
    }

    /**
     * Test method for {@link world.bentobox.islandfly.FlyToggleCommand#canExecute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testCanExecuteNotAllowedFlagNoPermission() {
        when(island.isAllowed(eq(user), any())).thenReturn(false);
        when(user.hasPermission(anyString())).thenReturn(false);
        assertFalse(ftc.canExecute(user, "fly", Collections.emptyList()));
        verify(user).sendMessage("islandfly.command.not-allowed-fly");
    }
    /**
     * Test method for {@link world.bentobox.islandfly.FlyToggleCommand#canExecute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testCanExecuteNoFlagAllowedPermission() {
        when(island.isAllowed(eq(user), any())).thenReturn(false);
        when(user.hasPermission(anyString())).thenReturn(true);
        assertTrue(ftc.canExecute(user, "fly", Collections.emptyList()));
        verify(user, never()).sendMessage(anyString());
    }
    /**
     * Test method for {@link world.bentobox.islandfly.FlyToggleCommand#canExecute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testCanExecuteFlagAllowed() {
        when(island.isAllowed(eq(user), any())).thenReturn(true);
        when(user.hasPermission(anyString())).thenReturn(false);
        assertTrue(ftc.canExecute(user, "fly", Collections.emptyList()));
        verify(user, never()).sendMessage(anyString());
    }

    /**
     * Test method for {@link world.bentobox.islandfly.FlyToggleCommand#canExecute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testCanExecuteOutsideProtectionRange() {
        when(island.isAllowed(eq(user), any())).thenReturn(true);
        when(user.hasPermission(anyString())).thenReturn(false);
        when(box.contains(any(Vector.class))).thenReturn(false);
        assertFalse(ftc.canExecute(user, "fly", Collections.emptyList()));
        verify(user).sendMessage("islandfly.outside-protection-range");
    }

    /**
     * Test method for {@link world.bentobox.islandfly.FlyToggleCommand#canExecute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testCanExecuteOutsideProtectionRangeCommandAllowed() {
        settings.setAllowCommandOutsideProtectionRange(true);
        when(island.isAllowed(eq(user), any())).thenReturn(true);
        when(user.hasPermission(anyString())).thenReturn(false);
        when(box.contains(any(Vector.class))).thenReturn(false);
        assertTrue(ftc.canExecute(user, "fly", Collections.emptyList()));
        verify(user, never()).sendMessage(anyString());
    }

    /**
     * Test method for {@link world.bentobox.islandfly.FlyToggleCommand#execute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test //I don't know what to do here
    public void testExecuteUserStringListOfStringAllowedFlight() {
        when(p.getAllowFlight()).thenReturn(true);
        ftc.execute(user, "fly", Collections.emptyList());
        verify(p).setFlying(false);
        verify(p).setAllowFlight(false);
        verify(user).sendMessage("islandfly.disable-fly");
    }
    /**
     * Test method for {@link world.bentobox.islandfly.FlyToggleCommand#execute(world.bentobox.bentobox.api.user.User, java.lang.String, java.util.List)}.
     */
    @Test
    public void testExecuteUserStringListOfStringNotAllowedFlight() {
        ftc.execute(user, "fly", Collections.emptyList());
        verify(p).setAllowFlight(true);
        verify(user).sendMessage("islandfly.enable-fly");
    }
}
