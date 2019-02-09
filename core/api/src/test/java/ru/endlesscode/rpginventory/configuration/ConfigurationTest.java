package ru.endlesscode.rpginventory.configuration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.endlesscode.rpginventory.configuration.ConfigurationProvider;
import ru.endlesscode.rpginventory.misc.TestConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class ConfigurationTest {

    private Path tmpDir;
    private ConfigurationProvider<TestConfiguration> testConfiguration;

    @Before
    public void setUp() throws Exception {
        Path testDir = Files.createDirectories(Paths.get("testFiles"));
        this.tmpDir = Files.createTempDirectory(testDir, "cfg");
        this.initConfigurationTest(); // Ducking junit. Don't blame me.
    }

    @Test
    public void initConfigurationTest() {
        this.testConfiguration = new ConfigurationProvider<>(this.tmpDir, TestConfiguration.class);
    }

    @Test()
    public void hasConfigurationSaved() {
        assert Files.exists(this.tmpDir.resolve("testConfiguration.conf"));
    }

    @Test
    public void isConfigObjectLoadedProperly() {
        final TestConfiguration config = this.testConfiguration.getConfig();
        Assert.assertNotNull(config);
    }

    @Test
    public void isConfigVariablesLoadedProperly() {
        Assert.assertNotNull(this.testConfiguration);

        final TestConfiguration local = new TestConfiguration();
        final TestConfiguration config = this.testConfiguration.getConfig();
        Assert.assertEquals(local.getaString(), config.getaString());
        Assert.assertEquals(local.getAnInt(), config.getAnInt());
    }

    @Test
    public void variablesEditTest() {
        final String newString = "Lorem ipsum dolor sit amet, consectetur.";

        TestConfiguration config = this.testConfiguration.getConfig();
        config.setAnInt(6);
        config.setaString(newString);
        this.testConfiguration.save();
        this.testConfiguration.reload();

        config = this.testConfiguration.getConfig();
        Assert.assertEquals(newString, config.getaString());
        Assert.assertEquals(6, config.getAnInt());
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
}
