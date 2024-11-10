package world.bentobox.islandfly.listeners;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;
import org.bukkit.scheduler.BukkitScheduler;
import org.eclipse.jdt.annotation.NonNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.md_5.bungee.api.chat.TextComponent;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.events.flags.FlagProtectionChangeEvent;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
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
public class FlyFlagListenerTest {
    
    private FlyFlagListener ffl;
    @Mock
    private BentoBox plugin;
    @Mock
    private IslandFlyAddon addon;
    @Mock
    private Settings settings;
    @Mock
    private BukkitScheduler scheduler;
    @Mock
    private FlagProtectionChangeEvent e;
    @Mock
    private Player p1;
    @Mock
    private Player p2;
    @Mock
    private Player p3;
    @Mock
    private Player op;
    @Mock
    private Island island;
    @Mock
    private Spigot spigot;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        when(addon.getPlugin()).thenReturn(plugin);
        // Locales
        User.setPlugin(plugin);
        LocalesManager lm = mock(LocalesManager.class);
        when(plugin.getLocalesManager()).thenReturn(lm);
        when(lm.get(any(), anyString())).thenAnswer((Answer<String>) invocation -> invocation.getArgument(1, String.class));
        PlaceholdersManager phm = mock(PlaceholdersManager.class);
        when(plugin.getPlaceholdersManager()).thenReturn(phm);
        when(phm.replacePlaceholders(any(), anyString())).thenAnswer((Answer<String>) invocation -> invocation.getArgument(1, String.class));

        // Settings
        when(settings.getFlyTimeout()).thenReturn(5);
        when(addon.getSettings()).thenReturn(settings);
        // Bukkit
        PowerMockito.mockStatic(Bukkit.class);
        when(Bukkit.getScheduler()).thenReturn(scheduler);

        // Event
        when(e.getEditedFlag()).thenReturn(IslandFlyAddon.ISLAND_FLY_PROTECTION);
        when(e.getIsland()).thenReturn(island);
        @NonNull
        List<Player> list = new ArrayList<>();
        when(p1.getUniqueId()).thenReturn(UUID.randomUUID());
        when(p1.spigot()).thenReturn(spigot);
        User.getInstance(p1);
        when(p1.isFlying()).thenReturn(true);
       when(p2.getUniqueId()).thenReturn(UUID.randomUUID());
       when(p2.spigot()).thenReturn(spigot);
        User.getInstance(p2);
        when(p2.isFlying()).thenReturn(true);
        when(p2.isOnline()).thenReturn(true);
        when(p2.getLocation()).thenReturn(mock(Location.class));
         when(p3.getUniqueId()).thenReturn(UUID.randomUUID());
         when(p3.spigot()).thenReturn(spigot);
        User.getInstance(p3);
        when(p3.isFlying()).thenReturn(false);
        when(op.getUniqueId()).thenReturn(UUID.randomUUID());
        when(op.spigot()).thenReturn(spigot);
        User.getInstance(op);
        when(op.isFlying()).thenReturn(true);
        when(op.isOp()).thenReturn(true);
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(op);
        when(island.getPlayersOnIsland()).thenReturn(list);
        // One player is allowed, others not
        when(island.isAllowed(any(), any())).thenReturn(true, false);
        when(island.onIsland(any())).thenReturn(true);
        
        ffl = new FlyFlagListener(addon);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        User.clearUsers();
    }
    
    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyFlagListener#onFlagChange(world.bentobox.bentobox.api.events.flags.FlagProtectionChangeEvent)}.
     */
    @Test
    public void testOnFlagChangeOtherFlag() {
        FlagProtectionChangeEvent e = mock(FlagProtectionChangeEvent.class);
        Flag flag = mock(Flag.class);
        when(e.getEditedFlag()).thenReturn(flag);
        ffl.onFlagChange(e);
        verify(e, never()).getIsland();
    }
    
    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyFlagListener#onFlagChange(world.bentobox.bentobox.api.events.flags.FlagProtectionChangeEvent)}.
     */
    @Test
    public void testOnFlagChange() {
        ffl.onFlagChange(e);
        verify(p1, never()).sendMessage(anyString());
        this.checkSpigotMessage("islandfly.fly-turning-off-alert");
        verify(p3, never()).sendMessage(anyString());
        verify(op, never()).sendMessage(anyString());
        verify(scheduler).runTaskLater(eq(plugin), any(Runnable.class), eq(100L));
    }
    
    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyFlagListener#onFlagChange(world.bentobox.bentobox.api.events.flags.FlagProtectionChangeEvent)}.
     */
    @Test
    public void testOnFlagChangeZeroTime() {
        when(settings.getFlyTimeout()).thenReturn(0);
        ffl.onFlagChange(e);
        verify(p1, never()).sendMessage(anyString());
        this.checkSpigotMessage("islandfly.fly-turning-off-alert");
        verify(p3, never()).sendMessage(anyString());
        verify(op, never()).sendMessage(anyString());
        
        verify(p2).setFlying(false);
        verify(p2).setAllowFlight(false);
        checkSpigotMessage("islandfly.disable-fly");
        
    }

    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyFlagListener#disable(Player, User, Island)}.
     */
    @Test
    public void testDisableAllowedAgain() {
        when(island.isAllowed(any(), any())).thenReturn(true);
        ffl.disable(p2, User.getInstance(p2), island);
        checkSpigotMessage("islandfly.reallowed-fly");
    }
    
    /**
     * Test method for {@link world.bentobox.islandfly.listeners.FlyFlagListener#disable(Player, User, Island)}.
     */
    @Test
    public void testDisable() {
        when(island.isAllowed(any(), any())).thenReturn(false);
        ffl.disable(p2, User.getInstance(p2), island);
        this.checkSpigotMessage("islandfly.disable-fly");
    }

    /**
     * Check that spigot sent the message
     * @param message - message to check
     */
    public void checkSpigotMessage(String expectedMessage) {
        checkSpigotMessage(expectedMessage, 1);
    }

    public void checkSpigotMessage(String expectedMessage, int expectedOccurrences) {
        // Capture the argument passed to spigot().sendMessage(...) if messages are sent
        ArgumentCaptor<TextComponent> captor = ArgumentCaptor.forClass(TextComponent.class);

        // Verify that sendMessage() was called at least 0 times (capture any sent messages)
        verify(spigot, atLeast(0)).sendMessage(captor.capture());

        // Get all captured TextComponents
        List<TextComponent> capturedMessages = captor.getAllValues();

        // Count the number of occurrences of the expectedMessage in the captured messages
        long actualOccurrences = capturedMessages.stream().map(component -> component.toLegacyText()) // Convert each TextComponent to plain text
                .filter(messageText -> messageText.contains(expectedMessage)) // Check if the message contains the expected text
                .count(); // Count how many times the expected message appears

        // Assert that the number of occurrences matches the expectedOccurrences
        assertEquals("Expected message occurrence mismatch: " + expectedMessage, expectedOccurrences,
                actualOccurrences);
    }

}
