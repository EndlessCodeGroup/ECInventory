/*
 * This file is part of RPGInventory.
 * Copyright (C) 2017 EndlessCode Group and contributors
 *
 * RPGInventory is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RPGInventory is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RPGInventory.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.endlesscode.rpginventory.misc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.endlesscode.rpginventory.FileTestBase;


public class I18NTest extends FileTestBase {

    // SUT
    private I18N i18n;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        this.i18n = Mockito.spy(new SimpleI18N(tmpDir.toFile()));
    }

    @Test
    public void constructor_creatingDirectoryWithExistingFileMustThrowException() {
        try {
            // When
            new SimpleI18N(testDir.toFile());
        } catch (I18NException e) {
            // Then
            Assert.assertEquals("Failed to create locales folder", e.getMessage());
            return;
        }

        Assert.fail();
    }

    @Test
    public void reload_reloadingExistingLocaleMustBeSuccessful() {
        // When
        i18n.reload("test");
    }

    @Test
    public void reload_reloadingMustBeCaseInsensitive() {
        // When
        i18n.reload("TeSt");
    }

    @Test
    public void getMessage_byKey() {
        // When
        final String message = i18n.getMessage("key");

        // Then
        Assert.assertEquals("Something value", message);
        Mockito.verify(i18n, Mockito.never()).stripColor(ArgumentMatchers.anyString());
    }

    @Test
    public void getMessage_byKeyWithStripColor() {
        // When
        i18n.getMessage("key", true);

        // Then
        Mockito.verify(i18n).stripColor(ArgumentMatchers.anyString());
    }

    @Test
    public void getMessage_notExistingKeyMustReturnKey() {
        // Given
        final String key = "not.existing.key";

        // When
        final String message = i18n.getMessage(key);

        // Then
        Assert.assertEquals(key, message);
    }

    @Test
    public void getMessage_byKeyWithArgs() {
        // When
        final String message = i18n.getMessage("with.args", "Text", 1);

        // Then
        Assert.assertEquals("Args: Text, 1", message);
    }

}
