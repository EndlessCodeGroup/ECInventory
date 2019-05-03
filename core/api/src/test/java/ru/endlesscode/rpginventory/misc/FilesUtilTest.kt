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

import org.hamcrest.CoreMatchers;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import ru.endlesscode.rpginventory.FileTestBase;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collections;

public class FilesUtilTest extends FileTestBase {

    @Test
    public void copyResourceToFile_existingResourceToNewFileMustBeSuccessful() throws Exception {
        this.copyResourceToFile("/resource", tmpDir.resolve("resource"));
    }

    @Test
    public void copyResourceToFile_resourceWithoutStartingSlashMustBeSuccessful() throws Exception {
        this.copyResourceToFile("resource", tmpDir.resolve("resource"));
    }

    @Test
    public void copyResourceToFile_existingResourceToExistingFileMustThrowException() throws Exception {
        // Given
        Path target = testDir.resolve("existingFile");
        try {
            // When
            this.copyResourceToFile("/resource", target);
        } catch (IllegalArgumentException e) {
            // Then
            String expectedMessage = String.format(
                    "Failed to copy \"/resource\" to given target: \"%s\"",
                    target.toAbsolutePath().toString()
            );
            Assert.assertEquals(expectedMessage, e.getMessage());
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(FileAlreadyExistsException.class));
            return;
        }

        Assert.fail();
    }

    @Test
    public void copyResourceToFile_notExistingResourceToNewFileMustThrowException() throws Exception {
        // Given
        final Path target = tmpDir.resolve("newFile");

        try {
            // When
            this.copyResourceToFile("/notExistingResource", target);
        } catch (IllegalArgumentException e) {
            // Then
            Assert.assertEquals("Resource file \"/notExistingResource\" not exists", e.getMessage());
            Assert.assertNull(e.getCause());
            return;
        }

        Assert.fail();
    }

    private void copyResourceToFile(@NotNull String resource, @NotNull Path targetFile) throws IOException {
        // When
        FilesUtil.copyResourceToFile(resource, targetFile);

        // Then
        assertFileContentEquals(targetFile, "This is a test resource file.", "Это тестовый файл ресурсов.");
    }

    @Test
    public void readFileToString_existingFileMustBeSuccessful() {
        // Given
        Path target = testDir.resolve("existingFile");

        // When
        final String content = FilesUtil.readFileToString(target);

        // Then
        Assert.assertEquals("Multi-line\nexisting\nfile.\nС русским\nтекстом.", content);
    }

    @Test
    public void readFileToString_notExistingFileMustThrowException() {
        // Given
        Path target = testDir.resolve("notExistingFile");
        try {
            // When
            FilesUtil.readFileToString(target);
        } catch (IllegalArgumentException e) {
            // Then
            String expectedMessage = String.format(
                    "Given file \"%s\" can't be read",
                    target.toAbsolutePath().toString()
            );
            Assert.assertEquals(expectedMessage, e.getMessage());
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(NoSuchFileException.class));
            return;
        }

        Assert.fail();
    }

    @Test
    public void mergeFiles_existingDirectoryShouldBeSuccessful() throws Exception {
        // Given
        createFile("1oneFile", "Line one");
        createFile("dir/2anotherFile", "Line two");
        createFile("dir/3thirdFile", "Line 3");

        // When
        Path result = FilesUtil.mergeFiles(tmpDir, path -> true);

        // Then
        assertFileContentEquals(result, "Line one", "Line two", "Line 3");
    }

    @Test
    public void mergeFiles_withPredicateShouldMergeOnlyMatchFiles() throws Exception {
        // Given
        createFile("file.merge", "Line one");
        createFile("dir/fileTwo", "Skipped line");
        createFile("dir/fileThree.merge", "Line 3");

        // When
        Path result = FilesUtil.mergeFiles(tmpDir, path -> path.toString().endsWith(".merge"));

        // Then
        assertFileContentEquals(result, "Line one", "Line 3");
    }

    @Test
    public void mergeFiles_emptyDirectoryShouldReturnEmptyFile() throws Exception {
        // When
        Path result = FilesUtil.mergeFiles(tmpDir, path -> true);

        // Then
        assertFileContentEquals(result, Collections.emptyList());
    }

    @Test
    public void mergeFiles_notDirectoryShouldThrowException() {
        // Given
        Path file = testDir.resolve("existingFile");

        try {
            // When
            FilesUtil.mergeFiles(file, path -> true);
        } catch (Exception e) {
            // Then
            String expectedMessage = String.format(
                    "Files in given directory \"%s\" can't be merged",
                    file.toAbsolutePath().toString()
            );
            Assert.assertEquals(expectedMessage, e.getMessage());
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(FileSystemException.class));
            return;
        }

        Assert.fail();
    }
}
