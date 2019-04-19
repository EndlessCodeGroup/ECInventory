package ru.endlesscode.rpginventory.configuration;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;
import ru.endlesscode.rpginventory.FileTestBase;
import ru.endlesscode.rpginventory.item.ConfigurableItemStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationCollectorTest extends FileTestBase {

    private final Map<String, String> stringValues = new HashMap<String, String>() {{
        this.put("first", "Nulla semper facilisis urna non fermentum.");
        this.put("second", "Morbi at lorem vitae odio molestie scelerisque.");
        this.put("third", "Vivamus non neque nec purus auctor hendrerit.");
        this.put("fourth", "Integer nec auctor ipsum, porttitor dictum sapien.");
    }};

    private final Map<String, ConfigurableItemStack> cisValues = new HashMap<String, ConfigurableItemStack>() {{
        this.put("stick", ConfigurableItemStack.Builder.fromMaterial("STICK").build());
        this.put("magicStick", ConfigurableItemStack.Builder.fromMaterial("STICK")
                .withDisplayName("&6Magic stick")
                .withLore(
                        Arrays.asList(
                                "&7This stick can be obtained in",
                                "&7the &cElite dungeon&7 after defeating",
                                "&7a &4Bloody swordmaster&7."
                        )
                ).build()
        );
        this.put("uselessStick", ConfigurableItemStack.Builder.fromMaterial("STICK")
                .withDisplayName("&7Useless stick")
                .withLore(
                        Arrays.asList(
                                "&7This stick can be obtained everywhere,",
                                "&7where a tree available."
                        )
                ).build()
        );
        this.put("justStick", ConfigurableItemStack.Builder.fromMaterial("STICK")
                .withDisplayName("&aJust stick")
                .withLore(
                        Collections.singletonList("&7Where you found it?..")
                ).build()
        );
    }};

    private static <K, V> TypeToken<Map<K, V>> mapToken(Class<K> kClass, Class<V> vClass) {
        // @formatter:off
        return new TypeToken<Map<K, V>>() {}
                .where(new TypeParameter<K>() {}, kClass)
                .where(new TypeParameter<V>() {}, vClass);
        // @formatter:on
    }

    @Test
    public void stringValuesRead() {
        // Given
        this.saveResource(this.tmpDir, "stringValues.conf");
        final ConfigurationCollector collector = new ConfigurationCollector(this.tmpDir.toFile());

        // When
        final Map<String, String> collect = collector.collect(mapToken(String.class, String.class));

        // Then
        Assert.assertEquals(this.stringValues, collect);
    }

    @Test
    public void cisValuesRead() {
        // Given
        this.saveResource(this.tmpDir, "cisValues.conf");
        final ConfigurationCollector collector = new ConfigurationCollector(this.tmpDir.toFile());

        // When
        final Map<String, ConfigurableItemStack> collect = collector.collect(mapToken(String.class, ConfigurableItemStack.class));

        // Then
        Assert.assertEquals(this.cisValues, collect);
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
