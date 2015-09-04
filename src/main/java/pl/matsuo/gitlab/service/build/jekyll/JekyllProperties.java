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


  /**
   * @param raw if it is documentation only repository root of repository is root of documentation; for maven projects,
   *            default root of documentation is src/site
   * @return relative source path
   */
  public String source(boolean raw) {
    return properties.getProperty("jekyll.source", raw ? "." : "src/site");
  }


  public String destination() {
    return properties.getProperty("jekyll.destination", "_site");
  }


  /**
   * Returns url to project denoting base project style. Defaults to kosher-base-style project on github.
   */
  public String styleRepository() {
    return properties.getProperty("jekyll.styleRepository", "https://github.com/tunguski/kosher-base-style.git");
  }


  /**
   * Returns branch from style repository that should be used.
   */
  public String styleBranch() {
    return properties.getProperty("jekyll.styleBranch", "master");
  }


  /**
   * Returns style directory to which style repository should be checked out.
   */
  public String styleDirectory() {
    return properties.getProperty("jekyll.styleDirectory", ".style");
  }
}

