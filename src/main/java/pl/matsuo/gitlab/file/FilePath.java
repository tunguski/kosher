package pl.matsuo.gitlab.file;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.function.Consumer;

import static org.apache.commons.io.FileUtils.*;
import static pl.matsuo.gitlab.file.FileConverterProviderUtil.*;


/**
 * Created by marek on 29.08.15.
 */
public class FilePath {


  private final File file;
  FileConverterProvider fileConverterProvider = singleClassProvider(String.class, new StringFileConverter());


  private FilePath(String path) {
    file = new File(path);
  }


  private FilePath(File file) {
    this.file = file;
  }


  public static FilePath file(String ... path) {
    return new FilePath(String.join("/", path));
  }


  public static FilePath file(File file) {
    return new FilePath(file);
  }


  public static FilePath file(File file, String ... path) {
    return new FilePath(new File(file, String.join("/", path)));
  }


  /**
   * Execute if file does exist.
   */
  public FilePath with(Consumer<FilePath> execute) {
    if (file.exists()) {
      execute.accept(this);
    }
    return this;
  }


  /**
   * Execute if file does exist.
   */
  public FilePath configure(FileConverterProvider fileConverterProvider) {
    FilePath newPath = new FilePath(file);
    newPath.fileConverterProvider = fileConverterProvider;
    return newPath;
  }


  /**
   * Execute if file denoted by subPath does exist. Returns this subPaths' FilePath object.
   */
  public FilePath with(String subPath, Consumer<FilePath> execute) {
    return new FilePath(new File(file, subPath)).configure(fileConverterProvider).with(execute);
  }


  /**
   * Execute if file does not exist.
   */
  public FilePath without(String ... files) {
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


  /**
   * Execute if file does not exist.
   */
  public FilePath without(Consumer<FilePath> execute) {
    if (!file.exists()) {
      execute.accept(this);
    }
    return this;
  }


  /**
   * Execute if file does not exist.
   */
  public FilePath without(String subPath, Consumer<FilePath> execute) {
    return new FilePath(new File(file, subPath)).configure(fileConverterProvider).without(execute);
  }


  public FilePath content(Consumer execute) {
    if (file.exists()) {
      try {
        execute.accept(readFileToString(file));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return this;
  }


  public <E> FilePath content(Class<E> clazz, Consumer<E> execute) {
    if (file.exists()) {
      try {
        execute.accept(fileConverterProvider.converter(clazz).convert(new FileInputStream(file)));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return this;
  }


  public FilePath overwrite(String body) {
    try {
      FileUtils.write(file, body);
      return this;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public FilePath append(String body) {
    try {
      FileUtils.write(file, body, true);
      return this;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public FilePath appendln(String body) {
    try {
      FileUtils.write(file, body + "\n", true);
      return this;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public String toString() {
    return file.getAbsolutePath();
  }
}

