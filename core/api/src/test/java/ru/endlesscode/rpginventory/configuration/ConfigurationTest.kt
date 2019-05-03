package ru.endlesscode.rpginventory.configuration

import ru.endlesscode.rpginventory.FileTestBase
import ru.endlesscode.rpginventory.misc.TestConfiguration
import java.nio.file.Files
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ConfigurationTest : FileTestBase() {

    // SUT
    private lateinit var testConfiguration: ConfigurationProvider<TestConfiguration>

    @BeforeTest
    override fun setUp() {
        super.setUp()

        this.testConfiguration = ConfigurationProvider(this.tmpDir, TestConfiguration::class.java)
    }

    @Test
    fun hasConfigurationSaved() {
        // Given
        val configurationFile = this.tmpDir.resolve("testConfiguration.conf")

        // Then
        assertTrue(Files.exists(configurationFile))
    }

    @Test
    fun isConfigObjectLoadedProperly() {
        // When
        val config = this.testConfiguration.config

        // Then
        assertNotNull(config)
    }

    @Test
    fun isConfigVariablesLoadedProperly() {
        // Given
        val local = TestConfiguration()

        // When
        val config = this.testConfiguration.config

        // Then
        assertEquals(local.aString, config.aString)
        assertEquals(local.anInt.toLong(), config.anInt.toLong())
    }

    @Test
    fun variablesEditTest() {
        // Given
        val newInt = 6
        val newString = "Lorem ipsum dolor sit amet, consectetur."
        var config = this.testConfiguration.config

        // When
        config.anInt = newInt
        config.aString = newString
        this.testConfiguration.save()
        this.testConfiguration.reload()
        config = this.testConfiguration.config

        // Then
        assertEquals(newString, config.aString)
        assertEquals(newInt.toLong(), config.anInt.toLong())
    }
}
