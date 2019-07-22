package pl.matsuo.gitlab.file;

import static org.apache.commons.io.FileUtils.*;
import static pl.matsuo.gitlab.file.FileConverterProviderUtil.*;
import static pl.matsuo.gitlab.function.FunctionalUtil.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.function.Consumer;

/** Created by marek on 29.08.15. */
public class FilePath {

  private final File file;
  FileConverterProvider fileConverterProvider =
      singleClassProvider(String.class, new StringFileConverter());

  private FilePath(String path) {
    file = new File(path);
  }

  private FilePath(File file) {
    this.file = file;
  }

  public static FilePath file(String... path) {
    return new FilePath(String.join("/", path));
  }

  public static FilePath file(File file) {
    return new FilePath(file);
  }

  public static FilePath file(File file, String... path) {
    return new FilePath(new File(file, String.join("/", path)));
  }

  protected FilePath execAndReturnThis(boolean shouldExec, Runnable runnable) {
    if (shouldExec) {
      runnable.run();
    }
    return this;
  }

  /** Execute if file does exist. */
  public FilePath with(Consumer<FilePath> execute) {
    return execAndReturnThis(file.exists(), () -> execute.accept(this));
  }

  /** Execute if file does exist. */
  public FilePath configure(FileConverterProvider fileConverterProvider) {
    FilePath newPath = new FilePath(file);
    newPath.fileConverterProvider = fileConverterProvider;
    return newPath;
  }

  /** Execute if file denoted by subPath does exist. Returns this subPaths' FilePath object. */
  public FilePath with(String subPath, Consumer<FilePath> execute) {
    return new FilePath(new File(file, subPath)).configure(fileConverterProvider).with(execute);
  }

  /** Execute if file does not exist. */
  public FilePath without(String... files) {
    if (files.length == 0) {
      throw new IllegalArgumentException();
    }

    for (String fileName : files) {
      if (new File(file, fileName).exists()) {
        return file(file, fileName);
      }
    }

    return file(file, files[files.length - 1]);
  }

  /** Execute if file does not exist. */
  public FilePath without(Consumer<FilePath> execute) {
    return execAndReturnThis(!file.exists(), () -> execute.accept(this));
  }

  /** Execute if file does not exist. */
  public FilePath without(String subPath, Consumer<FilePath> execute) {
    return new FilePath(new File(file, subPath)).configure(fileConverterProvider).without(execute);
  }

  public FilePath content(Consumer execute) {
    return execAndReturnThis(
        file.exists(), () -> runtimeEx(() -> execute.accept(readFileToString(file))));
  }

  public <E> FilePath content(Class<E> clazz, Consumer<E> execute) {
    return execAndReturnThis(
        file.exists(),
        () ->
            runtimeEx(
                () ->
                    execute.accept(
                        fileConverterProvider
                            .converter(clazz)
                            .convert(new FileInputStream(file)))));
  }

  public FilePath overwrite(String body) {
    return execAndReturnThis(true, () -> runtimeEx(() -> write(file, body)));
  }

  public FilePath append(String body) {
    return execAndReturnThis(true, () -> runtimeEx(() -> write(file, body, true)));
  }

  public FilePath appendln(String body) {
    return execAndReturnThis(true, () -> runtimeEx(() -> write(file, body + "\n", true)));
  }

  @Override
  public String toString() {
    return file.getAbsolutePath();
  }
}
