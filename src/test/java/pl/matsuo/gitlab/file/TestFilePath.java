package pl.matsuo.gitlab.file;

import static java.io.File.createTempFile;
import static java.nio.file.Files.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static pl.matsuo.gitlab.file.FilePath.*;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

/** Created by marek on 29.08.15. */
public class TestFilePath {

  @Test
  public void testFile() throws Exception {
    File tempFile = createTempFile("temp", ".tmp");
    FilePath file = file(tempFile);
    assertEquals(tempFile.getAbsolutePath(), file.toString());
  }

  @Test
  public void testFile1() throws Exception {
    FilePath file = file("/tmp");
    assertEquals("/tmp", file.toString());
  }

  @Test
  public void testFile2() throws Exception {
    File tempDir = createTempDirectory("test").toFile();
    FilePath file = file(tempDir, "testFile");
    assertEquals(tempDir.getAbsolutePath() + "/testFile", file.toString());
  }

  @Test
  public void testWith() throws Exception {
    FilePath file = file(createTempFile("temp", ".tmp"));
    AtomicBoolean executed = new AtomicBoolean(false);
    file.with(path -> executed.set(true));
    assertTrue(executed.get());
  }

  @Test
  public void testWith1() throws Exception {
    File tempDir = createTempDirectory("test").toFile();
    new File(tempDir, "testFile").createNewFile();

    FilePath file = file(tempDir);
    AtomicBoolean executed = new AtomicBoolean(false);
    file.with("testFile", path -> executed.set(true));
    assertTrue(executed.get());
  }

  @Test
  public void testWithout() throws Exception {
    File tempDir = createTempDirectory("test").toFile();

    FilePath file = file(new File(tempDir, "testFile"));
    AtomicBoolean executed = new AtomicBoolean(false);
    file.without(path -> executed.set(true));
    assertTrue(executed.get());
  }

  @Test
  public void testWithout1() throws Exception {
    File tempDir = createTempDirectory("test").toFile();

    FilePath file = file(tempDir);
    AtomicBoolean executed = new AtomicBoolean(false);
    file.without("testFile", path -> executed.set(true));
    assertTrue(executed.get());
  }

  @Test
  public void testContent() throws Exception {
    File tempDir = createTempDirectory("test").toFile();
    File testFile = new File(tempDir, "testFile");
    testFile.createNewFile();
    FileUtils.writeStringToFile(testFile, "data");

    FilePath file = file(tempDir);
    AtomicBoolean executed = new AtomicBoolean(false);
    file.with(
        "testFile",
        path ->
            path.content(
                content -> {
                  executed.set(true);
                  assertEquals("data", content);
                }));

    assertTrue(executed.get());
  }

  @Test
  public void testConfigureContent() throws Exception {
    File tempDir = createTempDirectory("test").toFile();
    File testFile = new File(tempDir, "testFile");
    testFile.createNewFile();
    FileUtils.writeStringToFile(testFile, "data");

    FileConverter<Integer> converter = mock(FileConverter.class);
    when(converter.convert(any(InputStream.class))).thenReturn(111);
    FileConverterProvider provider = mock(FileConverterProvider.class);
    when(provider.converter(Integer.class)).thenReturn(converter);

    AtomicBoolean executed = new AtomicBoolean(false);
    file(tempDir)
        .configure(provider)
        .with(
            "testFile",
            path ->
                path.content(
                    Integer.class,
                    content -> {
                      executed.set(true);
                      assertEquals((Integer) 111, content);
                    }));

    assertTrue(executed.get());
  }

  @Test
  public void testAppend() throws Exception {
    File tempDir = createTempDirectory("test").toFile();
    File testFile = new File(tempDir, "testFile");
    testFile.createNewFile();
    FileUtils.writeStringToFile(testFile, "data\n");

    FilePath file = file(testFile);
    file.append("data");

    AtomicBoolean executed = new AtomicBoolean(false);
    file.with(
        path ->
            path.content(
                content -> {
                  executed.set(true);
                  assertEquals("data\ndata", content);
                }));

    assertTrue(executed.get());
  }

  @Test
  public void testAppendln() throws Exception {
    File tempDir = createTempDirectory("test").toFile();
    File testFile = new File(tempDir, "testFile");
    testFile.createNewFile();
    FileUtils.writeStringToFile(testFile, "data\n");

    FilePath file = file(testFile);
    file.appendln("data");

    AtomicBoolean executed = new AtomicBoolean(false);
    file.with(
        path ->
            path.content(
                content -> {
                  executed.set(true);
                  assertEquals("data\ndata\n", content);
                }));

    assertTrue(executed.get());
  }

  @Test
  public void testOverwrite() throws Exception {
    File tempDir = createTempDirectory("test").toFile();
    File testFile = new File(tempDir, "testFile");
    testFile.createNewFile();
    FileUtils.writeStringToFile(testFile, "data");

    FilePath file = file(testFile);
    file.overwrite("data");

    AtomicBoolean executed = new AtomicBoolean(false);
    file.with(
        path ->
            path.content(
                content -> {
                  executed.set(true);
                  assertEquals("data", content);
                }));

    assertTrue(executed.get());
  }
}
