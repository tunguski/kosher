package pl.matsuo.gitlab.file;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by marek on 29.08.15.
 */
@Service
public final class YamlFileConverterProvider implements FileConverterProvider {


  ObjectMapper mapper = new ObjectMapper(new YAMLFactory());


  protected <E> E read(String text, Class<E> clazz) {
    try {
      return mapper.readValue(text, clazz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  protected <E> E read(InputStream stream, Class<E> clazz) {
    try {
      return mapper.readValue(stream, clazz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public <E> FileConverter<E> converter(final Class<E> clazz) {

    return new FileConverter<E>() {


      @Override
      public E convert(InputStream stream) {
        return read(stream, clazz);
      }


      @Override
      public E convert(String text) {
        return read(text, clazz);
      }
    };
  }
}

