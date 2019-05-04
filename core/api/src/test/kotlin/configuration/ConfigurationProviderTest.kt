package ru.endlesscode.rpginventory.configuration

import ru.endlesscode.rpginventory.FileTestBase
import java.nio.file.Files
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ConfigurationProviderTest : FileTestBase() {

    // SUT
    private lateinit var configurationProvider: ConfigurationProvider<TestConfiguration>

    @BeforeTest
    override fun setUp() {
        super.setUp()

        this.configurationProvider = ConfigurationProvider(this.dir, TestConfiguration::class.java)
    }

    @Test
    fun `when ConfigurationProvider created - configuration file should be created`() {
        // Given
        val configurationFile = this.dir.resolve("testConfiguration.conf")

        // Then
        assertTrue(Files.exists(configurationFile))
    }

    @Test
    fun `when ConfigurationProvider created - config should not be null`() {
        // When
        val config = this.configurationProvider.config

        // Then
        assertNotNull(config)
    }

    @Test
    fun `when ConfigurationProvider created - config should be loaded properly`() {
        // Given
        val local = TestConfiguration()

        // When
        val config = this.configurationProvider.config

        // Then
        assertEquals(local.aString, config.aString)
        assertEquals(local.anInt, config.anInt)
    }

    @Test
    fun `when edit configuration - and then reload it - should be loaded changed config`() {
        // Given
        val newInt = 6
        val newString = "Lorem ipsum dolor sit amet, consectetur."
        var config = this.configurationProvider.config

        // When
        config.anInt = newInt
        config.aString = newString
        this.configurationProvider.save()
        this.configurationProvider.reload()
        config = this.configurationProvider.config

        // Then
        assertEquals(newString, config.aString)
        assertEquals(newInt, config.anInt)
    }
}
