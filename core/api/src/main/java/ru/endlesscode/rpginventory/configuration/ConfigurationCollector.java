package ru.endlesscode.rpginventory.configuration;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import ru.endlesscode.rpginventory.misc.FilesUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigurationCollector {

    private static final String CONFIG_EXTENSION = ".conf";

    private final Path configurationsDirectory;

    public ConfigurationCollector(@NotNull final File dataDirectory) {
        this(dataDirectory.toPath());
    }

    public ConfigurationCollector(@NotNull final Path dataDirectory) {
        this.configurationsDirectory = dataDirectory;
        checkConfigurationDirectory();
    }

    public <T> T collect(TypeToken<T> typeToken) {
        checkConfigurationDirectory();

        final Path mergedConfig = FilesUtil.mergeFiles(
                configurationsDirectory,
                path -> path.getFileName().toString().toLowerCase().endsWith(CONFIG_EXTENSION)
        );

        try {
            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setPath(mergedConfig).build();
            CommentedConfigurationNode loaded = loader.load();
            return loaded.getValue(typeToken);
        } catch (ObjectMappingException | IOException e) {
            throw new ConfigurationException(e);
        }
    }

    private void checkConfigurationDirectory() {
        try {
            FilesUtil.makeSureDirectoryExists(this.configurationsDirectory);
        } catch (IOException e) {
            throw new ConfigurationException(
                    "\"" + configurationsDirectory.getFileName().toString() + "\" must be a directory.", e
            );
        }
    }
}
