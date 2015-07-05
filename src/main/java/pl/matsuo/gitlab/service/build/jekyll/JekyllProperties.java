package pl.matsuo.gitlab.service.build.jekyll;

import java.util.Properties;


/**
 * Created by marek on 05.07.15.
 */
public class JekyllProperties {


  public static String source(Properties properties) {
    return properties.getProperty("jekyll.source", "src/site");
  }


  public static String destination(Properties properties) {
    return properties.getProperty("jekyll.destination", "_site");
  }
}

