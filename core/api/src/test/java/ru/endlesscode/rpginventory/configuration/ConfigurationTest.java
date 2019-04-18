package ru.endlesscode.rpginventory.configuration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.endlesscode.rpginventory.FileTestBase;
import ru.endlesscode.rpginventory.misc.TestConfiguration;

import java.nio.file.Files;

public class ConfigurationTest extends FileTestBase {

    private ConfigurationProvider<TestConfiguration> testConfiguration;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
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
}
