package world.bentobox.islandfly.listeners;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.events.island.IslandEvent.IslandExitEvent;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.IslandWorldManager;
import world.bentobox.bentobox.managers.IslandsManager;
import world.bentobox.bentobox.managers.LocalesManager;
import world.bentobox.bentobox.managers.PlaceholdersManager;
import world.bentobox.islandfly.IslandFlyAddon;
import world.bentobox.islandfly.config.Settings;

/**
 * @author tastybento
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, BentoBox.class})
public class FlyListenerTest {
    @Mock
    private BentoBox plugin;
    @Mock
    private IslandFlyAddon addon;

    private FlyListener fl;
    @Mock
    private IslandsManager im;
    @Mock
    private User user;

    @Mock
    private Island island;
    @Mock
    private Island island2;
    private Optional<Island> opIsland2;
    @Mock
    private Player p;
    @Mock
    private @Nullable Location location;
    @Mock
    private IslandWorldManager iwm;
    @Mock
    private World world;
    @Mock
    private GameModeAddon gameMode;
    private UUID uuid;
    @Mock
    private BukkitScheduler sch;
    @Mock
    private Settings settings;

    /**
     */
    @Before
    public void setUp() {
        // Island manager
        when(addon.getIslands()).thenReturn(im);
        Optional<Island> opIsland1 = Optional.of(island);
        opIsland2 = Optional.of(island2);
        when(im.getProtectedIslandAt(any())).thenReturn(opIsland1);
        // Island
        when(island.isAllowed(any(), any())).thenReturn(true);
        when(island2.isAllowed(any(), any())).thenReturn(false);
        // User
        uuid = UUID.randomUUID();
        when(p.getUniqueId()).thenReturn(uuid);
        when(p.isOnline()).thenReturn(true);
        when(p.getLocation()).thenReturn(location);
        when(p.getWorld()).thenReturn(world);
        when(p.hasPermission(eq("bskyblock.island.fly"))).thenReturn(true);
        when(p.isOp()).thenReturn(false);
        when(p.isFlying()).thenReturn(true);
        User.setPlugin(plugin);
        User.getInstance(p);
        when(user.getUniqueId()).thenReturn(uuid);
        when(user.getPlayer()).thenReturn(p);
        when(user.isOnline()).thenReturn(true);
        when(user.getLocation()).thenReturn(location);
        when(user.getWorld()).thenReturn(world);
        when(user.hasPermission(eq("bskyblock.island.fly"))).thenReturn(true);
        // IWM
        when(addon.getPlugin()).thenReturn(plugin);
        when(plugin.getIWM()).thenReturn(iwm);
        Optional<GameModeAddon> opGm = Optional.of(gameMode);
        when(gameMode.getPermissionPrefix()).thenReturn("bskyblock.");
        when(iwm.getAddon(any())).thenReturn(opGm);
        when(iwm.getPermissionPrefix(any())).thenReturn("bskyblock.");
        // Bukkit
        PowerMockito.mockStatic(Bukkit.class);
        when(Bukkit.getScheduler()).thenReturn(sch);
        // settings
        when(settings.getFlyTimeout()).thenReturn(5);
        when(addon.getSettings()).thenReturn(settings);
        // Locales
        LocalesManager lm = mock(LocalesManager.class);
        when(lm.get(any(), any())).thenAnswer(invocation -> invocation.getArgument(1, String.class));
        when(plugin.getLocalesManager()).thenReturn(lm);
        PlaceholdersManager phm = mock(PlaceholdersManager.class);
        when(phm.replacePlaceholders(any(), any())).thenAnswer(invocation -> invocation.getArgument(1, String.class));
        // Placeholder manager
        when(plugin.getPlaceholdersManager()).thenReturn(phm);


        fl = new FlyListener(addon);
    }

    /**
     */
    @After
    public void tearDown() {
        User.clearUsers();
    }

    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyListener#onExitIsland(world.bentobox.bentobox.api.events.island.IslandEvent.IslandExitEvent)}.
     */
    @Test
    public void testOnExitIslandGraceTime() {
        when(im.getProtectedIslandAt(any())).thenReturn(Optional.empty());
        IslandExitEvent event = mock(IslandExitEvent.class);
        when(event.getPlayerUUID()).thenReturn(uuid);
        fl.onExitIsland(event);
        verify(sch).runTaskLater(eq(plugin), any(Runnable.class), eq(100L));
        verify(p).sendMessage(eq("islandfly.fly-outside-alert"));
    }

    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyListener#onExitIsland(world.bentobox.bentobox.api.events.island.IslandEvent.IslandExitEvent)}.
     */
    @Test
    public void testOnExitIslandGraceTimeOp() {
        when(p.isOp()).thenReturn(true);
        when(im.getProtectedIslandAt(any())).thenReturn(Optional.empty());
        IslandExitEvent event = mock(IslandExitEvent.class);
        when(event.getPlayerUUID()).thenReturn(uuid);
        fl.onExitIsland(event);
        verify(sch, never()).runTaskLater(eq(plugin), any(Runnable.class), any(Long.class));
        verify(p, never()).sendMessage(anyString());
    }

    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyListener#onExitIsland(world.bentobox.bentobox.api.events.island.IslandEvent.IslandExitEvent)}.
     */
    @Test
    public void testOnExitIslandGraceTimePermission() {
        when(p.hasPermission(eq("bskyblock.island.flybypass"))).thenReturn(true);
        when(im.getProtectedIslandAt(any())).thenReturn(Optional.empty());
        IslandExitEvent event = mock(IslandExitEvent.class);
        when(event.getPlayerUUID()).thenReturn(uuid);
        fl.onExitIsland(event);
        verify(sch, never()).runTaskLater(eq(plugin), any(Runnable.class), any(Long.class));
        verify(p, never()).sendMessage(anyString());
    }

    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyListener#onExitIsland(world.bentobox.bentobox.api.events.island.IslandEvent.IslandExitEvent)}.
     */
    @Test
    public void testOnExitIslandGraceTimeNotFlying() {
        when(user.hasPermission(eq("bskyblock.island.fly"))).thenReturn(true);
        when(p.isFlying()).thenReturn(false);
        when(im.getProtectedIslandAt(any())).thenReturn(Optional.empty());
        IslandExitEvent event = mock(IslandExitEvent.class);
        when(event.getPlayerUUID()).thenReturn(uuid);
        fl.onExitIsland(event);
        verify(sch).runTaskLater(eq(plugin), any(Runnable.class), eq(100L));
        verify(p, never()).sendMessage(eq("islandfly.fly-outside-alert"));
    }

    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyListener#onExitIsland(world.bentobox.bentobox.api.events.island.IslandEvent.IslandExitEvent)}.
     */
    @Test
    public void testOnExitIslandNoGraceTime() {
        when(im.getProtectedIslandAt(any())).thenReturn(Optional.empty());
        when(settings.getFlyTimeout()).thenReturn(0);
        IslandExitEvent event = mock(IslandExitEvent.class);
        when(event.getPlayerUUID()).thenReturn(uuid);
        fl.onExitIsland(event);
        verify(sch, never()).runTaskLater(eq(plugin), any(Runnable.class), any(Long.class));
        verify(p).sendMessage(eq("islandfly.disable-fly"));
    }

    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyListener#onExitIsland(world.bentobox.bentobox.api.events.island.IslandEvent.IslandExitEvent)}.
     */
    @Test
    public void testOnExitIslandNoGraceTimeNoPermission() {
        when(p.hasPermission(eq("bskyblock.island.fly"))).thenReturn(false);
        when(im.getProtectedIslandAt(any())).thenReturn(Optional.empty());
        when(settings.getFlyTimeout()).thenReturn(0);
        IslandExitEvent event = mock(IslandExitEvent.class);
        when(event.getPlayerUUID()).thenReturn(uuid);
        fl.onExitIsland(event);
        verify(sch, never()).runTaskLater(eq(plugin), any(Runnable.class), any(Long.class));
        verify(p, never()).sendMessage(eq("islandfly.disable-fly"));
    }


    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyListener#removeFly(world.bentobox.bentobox.api.user.User)}.
     */
    @Test
    public void testRemoveFlyUserFlyingNotOnline() {
        when(user.isOnline()).thenReturn(false);
        assertFalse(fl.removeFly(user));

    }

    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyListener#removeFly(world.bentobox.bentobox.api.user.User)}.
     */
    @Test
    public void testRemoveFlyUserFlyingOutsideProtectedIsland() {
        // If a player is flying outside an island into unowned space, then they should have their fly removed
        when(im.getProtectedIslandAt(any())).thenReturn(Optional.empty());
        assertTrue(fl.removeFly(user));
        verify(p).setFlying(false);
        verify(p).setAllowFlight(false);
        verify(user).sendMessage(eq("islandfly.disable-fly"));
    }

    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyListener#removeFly(world.bentobox.bentobox.api.user.User)}.
     */
    @Test
    public void testRemoveFlyUserFlyingBackInProtectedAreaOfIsland() {
        assertFalse(fl.removeFly(user));
        verify(user).sendMessage(eq("islandfly.cancel-disable"));
    }

    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyListener#removeFly(world.bentobox.bentobox.api.user.User)}.
     */
    @Test
    public void testRemoveFlyUserFlyingInOtherIslandNotAllowed() {
        when(im.getProtectedIslandAt(any())).thenReturn(opIsland2);
        assertTrue(fl.removeFly(user));
        verify(p).setFlying(false);
        verify(p).setAllowFlight(false);
        verify(user).sendMessage(eq("islandfly.disable-fly"));
    }

    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyListener#removeFly(world.bentobox.bentobox.api.user.User)}.
     */
    @Test
    public void testRemoveFlyUserFlyingInOtherProtectedIslandAllowed() {
        when(island2.isAllowed(any(), any())).thenReturn(true);
        when(im.getProtectedIslandAt(any())).thenReturn(opIsland2);
        assertFalse(fl.removeFly(user));
    }

    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyListener#removeFly(world.bentobox.bentobox.api.user.User)}.
     */
    @Test
    public void testRemoveFlyUserFlyingInOwnProtectedIslandNotAllowed() {
        when(island.isAllowed(any(), any())).thenReturn(false);
        assertTrue(fl.removeFly(user));
        verify(p).setFlying(false);
        verify(p).setAllowFlight(false);
        verify(user).sendMessage(eq("islandfly.disable-fly"));
    }


    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyListener#removeFly(world.bentobox.bentobox.api.user.User)}.
     */
    @Test
    public void testRemoveFlyUserFlyingInSpawnAllowed() {
        when(user.hasPermission(anyString())).thenReturn(true);
        when(island.isSpawn()).thenReturn(true);
        assertFalse(fl.removeFly(user));
    }

    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyListener#removeFly(world.bentobox.bentobox.api.user.User)}.
     */
    @Test
    public void testRemoveFlyUserFlyingInSpawnNotAllowed() {
        when(island.isSpawn()).thenReturn(true);
        assertTrue(fl.removeFly(user));
        verify(p).setFlying(false);
        verify(p).setAllowFlight(false);
        verify(user).sendMessage(eq("islandfly.disable-fly"));
    }

}
