package pl.matsuo.gitlab.file;

/**
 * Created by marek on 29.08.15.
 */
public class FileConverterProviderUtil {


  public static <E> FileConverterProvider singleClassProvider(Class<E> acceptedClass, FileConverter<E> converter) {
    return new FileConverterProvider() {
      @Override
      public <E> FileConverter<E> converter(Class<E> clazz) {
        if (acceptedClass.equals(clazz)) {
          return (FileConverter<E>) converter;
        } else {
          throw new IllegalArgumentException();
        }
      }
    };
  }
}
