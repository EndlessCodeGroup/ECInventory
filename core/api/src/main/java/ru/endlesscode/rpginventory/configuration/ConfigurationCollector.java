package ru.endlesscode.rpginventory.configuration;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationCollector {

    private final File configurationsDirectory;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public ConfigurationCollector(final File configurationsDirectory) {
        if (!configurationsDirectory.isDirectory()) {
            final File tmp = new File(
                    configurationsDirectory.getParentFile(),
                    configurationsDirectory.getName() + ".niceJoke." + System.currentTimeMillis() % 10_000
            );

            if (!configurationsDirectory.renameTo(tmp)) {
                throw new ConfigurationException(
                        "\"" + configurationsDirectory.getName() + "\" must be a directory."
                );
            }
        }
        if (!configurationsDirectory.exists()) {
            configurationsDirectory.mkdirs();
        }
        this.configurationsDirectory = configurationsDirectory;
    }

    public <K, V> Map<K, V> collect(Class<K> key, Class<V> value) {
        if (!this.configurationsDirectory.isDirectory()) {
            throw new ConfigurationException(
                    "\"" + configurationsDirectory.getName() + "\" must be a directory."
            );
        }
        final File[] files = this.configurationsDirectory.listFiles(
                pathname -> pathname.isFile() && pathname.getName().toLowerCase().endsWith(".conf")
        );
        if (files == null || files.length == 0) {
            return Collections.emptyMap();
        }

        final Map<K, V> result = new HashMap<>();
        HoconConfigurationLoader loader;
        CommentedConfigurationNode loaded;
        for (File file : files) {
            //TODO: Make it works
            loader = HoconConfigurationLoader.builder().setFile(file).build();
            Map<K, V> populate = new HashMap<>();
            try {
                loaded = loader.load();
                final Object loadedValue = loaded.getValue(result);
                if (!(loadedValue instanceof Map)) {
                    throw new ConfigurationException("Unexpected loaded value type " + loadedValue.getClass().getName());
                }

                final Map<K, V> values = loaded.getValue(toToken(key, value));
                System.out.println(values);
                result.putAll(values);
            } catch (ObjectMappingException | IOException e) {
                System.err.println(e);
                continue;
            }
        }

        return result;
    }

    private static <K, V> TypeToken<Map<K, V>> toToken(Class<K> kClass, Class<V> vClass){
        return new TypeToken<Map<K, V>>() {}
                .where(new TypeParameter<K>() {}, kClass)
                .where(new TypeParameter<V>() {}, vClass);
    }
}
