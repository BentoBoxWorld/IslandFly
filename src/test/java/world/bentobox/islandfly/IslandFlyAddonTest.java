package world.bentobox.islandfly;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.bukkit.Bukkit;
import org.eclipse.jdt.annotation.NonNull;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.AddonDescription;
import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.managers.AddonsManager;
import world.bentobox.bentobox.managers.FlagsManager;
import world.bentobox.islandfly.listeners.FlyDeathListener;
import world.bentobox.islandfly.listeners.FlyFlagListener;
import world.bentobox.islandfly.listeners.FlyListener;
import world.bentobox.islandfly.listeners.FlyLogoutListener;

/**
 * @author tastybento
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Bukkit.class, BentoBox.class, User.class})
public class IslandFlyAddonTest {

    private static File jFile;
    private IslandFlyAddon ifa;
    @Mock
    private BentoBox plugin;
    @Mock
    private AddonsManager am;
    @Mock
    private GameModeAddon gameMode;
    @Mock
    private FlagsManager fm;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        // Clean up
        tearDown();
        jFile = new File("addon.jar");
        Path original = Paths.get("src/main/resources/config.yml");
        Path path = Paths.get("config.yml");
        Files.copy(original, path);
        try (JarOutputStream tempJarOutputStream = new JarOutputStream(new FileOutputStream(jFile))) {
            //Added the new files to the jar.
            try (FileInputStream fis = new FileInputStream(path.toFile())) {

                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                JarEntry entry = new JarEntry(path.toString());
                tempJarOutputStream.putNextEntry(entry);
                while((bytesRead = fis.read(buffer)) != -1) {
                    tempJarOutputStream.write(buffer, 0, bytesRead);
                }
            }
        }
    }
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        // Set up plugin
        Whitebox.setInternalState(BentoBox.class, "instance", plugin);
        // Flags
        when(plugin.getFlagsManager()).thenReturn(fm);
        // Addons manager
        when(plugin.getAddonsManager()).thenReturn(am);
        // One game mode
        when(am.getGameModeAddons()).thenReturn(Collections.singletonList(gameMode));
        
        AddonDescription desc2 = new AddonDescription.Builder("bentobox", "BSkyBlock", "1.3").description("test").authors("tasty").build();
        when(gameMode.getDescription()).thenReturn(desc2);
        // Addon
        ifa = new IslandFlyAddon();

        File dataFolder = new File("addons/IslandFlyAddon");
        ifa.setDataFolder(dataFolder);
        ifa.setFile(jFile);
        AddonDescription desc = new AddonDescription.Builder("bentobox", "island fly addon", "1.3").description("test").authors("BONNe").build();
        ifa.setDescription(desc);
        // Player command
        CompositeCommand cmd = mock(CompositeCommand.class);
        @NonNull
        Optional<CompositeCommand> opCmd = Optional.of(cmd);
        when(gameMode.getPlayerCommand()).thenReturn(opCmd);
        // Settings
        
        
    }

    
    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDown() throws Exception {
        new File("addon.jar").delete();
        new File("config.yml").delete();
        deleteAll(new File("addons"));

    }

    private static void deleteAll(File file) throws IOException {
        if (file.exists()) {
            Files.walk(file.toPath())
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
        }

    }

    /**
     * Test method for {@link world.bentobox.islandfly.IslandFlyAddon#onEnable()}.
     */
    @Test
    public void testOnEnable() {
        ifa.onLoad();
        ifa.onEnable();
        verify(fm).registerFlag(eq(ifa), any());
        verify(am).registerListener(eq(ifa), any(FlyListener.class));
        verify(am).registerListener(eq(ifa), any(FlyDeathListener.class));
        verify(am).registerListener(eq(ifa), any(FlyLogoutListener.class));
        verify(am).registerListener(eq(ifa), any(FlyFlagListener.class));
    }
    
    /**
     * Test method for {@link world.bentobox.islandfly.IslandFlyAddon#onEnable()}.
     */
    @Test
    public void testOnEnableNoHook() {
        ifa.onLoad();
        ifa.getSettings().setDisabledGameModes(Collections.singleton("BSkyBlock"));
        ifa.onEnable();
        verify(fm, never()).registerFlag(eq(ifa), any());
        verify(am, never()).registerListener(eq(ifa), any(FlyListener.class));
        verify(am, never()).registerListener(eq(ifa), any(FlyDeathListener.class));
        verify(am, never()).registerListener(eq(ifa), any(FlyLogoutListener.class));
        verify(am, never()).registerListener(eq(ifa), any(FlyFlagListener.class));
    }

    /**
     * Test method for {@link world.bentobox.islandfly.IslandFlyAddon#onReload()}.
     */
    @Test
    public void testOnReloadHooked() {
        testOnEnable();
        ifa.onReload();
        verify(plugin).log(eq("[island fly addon] IslandFly addon reloaded."));
    }
    
    /**
     * Test method for {@link world.bentobox.islandfly.IslandFlyAddon#onReload()}.
     */
    @Test
    public void testOnReloadNotHooked() {
        ifa.onReload();
        verify(plugin, never()).log(anyString());
    }

    /**
     * Test method for {@link world.bentobox.islandfly.IslandFlyAddon#getSettings()}.
     */
    @Test
    public void testGetSettings() {
        assertNull(ifa.getSettings());
    }

}
