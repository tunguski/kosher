package pl.matsuo.gitlab.file;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

/** Created by marek on 29.08.15. */
public class StringFileConverter implements FileConverter<String> {

  @Override
  public String convert(InputStream stream) {
    try {
      return IOUtils.toString(stream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String convert(String text) {
    return text;
  }
}
