package ru.endlesscode.rpginventory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.endlesscode.rpginventory.configuration.ConfigurationCollector;
import ru.endlesscode.rpginventory.item.ConfigurableItemStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ConfigurationCollectorTest {

    private final Map<String, String> stringValues = new HashMap<String, String>() {{
        this.put("first", "Nulla semper facilisis urna non fermentum.");
        this.put("second", "Morbi at lorem vitae odio molestie scelerisque.");
        this.put("third", "Vivamus non neque nec purus auctor hendrerit.");
        this.put("fourth", "Integer nec auctor ipsum, porttitor dictum sapien.");
    }};

    private final Map<String, ConfigurableItemStack> cisValues = new HashMap<String, ConfigurableItemStack>() {{
        this.put("magicStick", ConfigurableItemStack.Builder.fromMaterial("STICK")
                .withDisplayName("&6Magic stick").withLore(
                        Arrays.asList(
                                "&7This stick can be obtained in",
                                "&7the &cElite dungeon&7 after defeating",
                                "&7a &4Bloody swordmaster&7."
                        )
                ).build()
        );
        this.put("uselessStick", ConfigurableItemStack.Builder.fromMaterial("STICK")
                .withDisplayName("&7Useless stick").withLore(
                        Arrays.asList(
                                "&7This stick can be obtained everywhere,",
                                "&7where a tree available."
                        )
                ).build()
        );
        this.put("justStick", ConfigurableItemStack.Builder.fromMaterial("STICK")
                .withDisplayName("&aJust stick").withLore(
                        Collections.singletonList("&7Where you found it?..")
                ).build()
        );
    }};

    private Path tmpDir;

    @Before
    public void setUp() throws Exception {
        Path testDir = Files.createDirectories(Paths.get("testFiles"));
        this.tmpDir = Files.createTempDirectory(testDir, "cfg");
    }

    @Test
    public void stringValuesRead() {
        this.saveResource(this.tmpDir, "stringValues.conf");
        final ConfigurationCollector collector = new ConfigurationCollector(this.tmpDir.toFile());
        final Map<String, String> collect = collector.collect(String.class, String.class);
        if (!collect.keySet().containsAll(this.stringValues.keySet())) {
            Assert.fail();
        }
        if (!collect.values().containsAll(this.stringValues.values())) {
            Assert.fail();
        }
    }

    @Test
    public void cisValuesRead() {
        this.saveResource(this.tmpDir, "cisValues.conf");
        final ConfigurationCollector collector = new ConfigurationCollector(this.tmpDir.toFile());
        final Map<String, ConfigurableItemStack> collect = collector.collect(String.class, ConfigurableItemStack.class);
        System.out.println(collect);
        if (!collect.keySet().containsAll(this.stringValues.keySet())) {
            Assert.fail();
        }
        if (!collect.values().containsAll(this.stringValues.values())) {
            Assert.fail();
        }
    }

    @After
    public void tearDown() throws Exception {
        Files.walk(this.tmpDir)
                .sorted(Comparator.reverseOrder())
                .forEach(this::deleteFile);
    }

    private void deleteFile(Path path) {
        try {
            Files.delete(path);
        } catch (IOException ignored) {
        }
    }

    private void saveResource(Path targetDirectory, String name) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(name);
        if (resourceAsStream == null) {
            return;
        }
        final Path resolve = targetDirectory.resolve(name);
        try {
            Files.copy(resourceAsStream, resolve, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ignore) {
            System.err.println("Failed to save" + name);
        }

    }
}
