package pl.matsuo.gitlab.service.build.jekyll;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Created by marek on 05.07.15.
 */
public class JekyllProperties {


  private final Properties properties;


  public JekyllProperties(Properties properties) {
    this.properties = properties;
  }


  public JekyllProperties(File file) throws IOException {
    this.properties = new Properties();
    properties.load(new FileInputStream(file));
  }


  public String source() {
    return properties.getProperty("jekyll.source", "src/site");
  }


  public String destination() {
    return properties.getProperty("jekyll.destination", "_site");
  }


  public boolean generateTemplate() {
    return Boolean.getBoolean(properties.getProperty("jekyll.generateTemplate", "true"));
  }
}

