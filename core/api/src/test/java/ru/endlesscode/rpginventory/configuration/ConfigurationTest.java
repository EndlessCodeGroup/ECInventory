package ru.endlesscode.rpginventory.configuration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.endlesscode.rpginventory.FileTestBase;
import ru.endlesscode.rpginventory.misc.TestConfiguration;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigurationTest extends FileTestBase {

    // SUT
    private ConfigurationProvider<TestConfiguration> testConfiguration;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.testConfiguration = new ConfigurationProvider<>(this.tmpDir, TestConfiguration.class);
    }

    @Test()
    public void hasConfigurationSaved() {
        // Given
        final Path configurationFile = this.tmpDir.resolve("testConfiguration.conf");

        // Then
        Assert.assertTrue(Files.exists(configurationFile));
    }

    @Test
    public void isConfigObjectLoadedProperly() {
        // When
        final TestConfiguration config = this.testConfiguration.getConfig();

        // Then
        Assert.assertNotNull(config);
    }

    @Test
    public void isConfigVariablesLoadedProperly() {
        // Given
        final TestConfiguration local = new TestConfiguration();

        // When
        final TestConfiguration config = this.testConfiguration.getConfig();

        // Then
        Assert.assertEquals(local.getaString(), config.getaString());
        Assert.assertEquals(local.getAnInt(), config.getAnInt());
    }

    @Test
    public void variablesEditTest() {
        // Given
        final int newInt = 6;
        final String newString = "Lorem ipsum dolor sit amet, consectetur.";
        TestConfiguration config = this.testConfiguration.getConfig();

        // When
        config.setAnInt(newInt);
        config.setaString(newString);
        this.testConfiguration.save();
        this.testConfiguration.reload();
        config = this.testConfiguration.getConfig();

        // Then
        Assert.assertEquals(newString, config.getaString());
        Assert.assertEquals(newInt, config.getAnInt());
    }
}
