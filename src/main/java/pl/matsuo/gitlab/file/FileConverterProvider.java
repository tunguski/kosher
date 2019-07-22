package pl.matsuo.gitlab.file;

/** Created by marek on 29.08.15. */
public interface FileConverterProvider {

  <E> FileConverter<E> converter(Class<E> clazz);
}
