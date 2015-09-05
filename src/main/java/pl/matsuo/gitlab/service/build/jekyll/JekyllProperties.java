package pl.matsuo.gitlab.service.build.jekyll;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import pl.matsuo.gitlab.service.build.jekyll.model.SiteConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Created by marek on 05.07.15.
 */
public class JekyllProperties {


  private final static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
  private final SiteConfig config;
  private final String sourceBase;


  public JekyllProperties(File config) {
    try {
      this.config = readConfig(config);
      sourceBase = new File(config.getParent(), "pom.xml").exists() ? "src/site" : ".";
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public static SiteConfig readConfig(File config) throws IOException {
    JsonNode jsonNode = mapper.readTree(config);
    JsonNode jekyll = jsonNode.get("site");
    return mapper.readValue(jekyll.toString(), SiteConfig.class);
  }


  public static SiteConfig readConfig(String config) throws IOException {
    JsonNode jsonNode = mapper.readTree(config);
    JsonNode jekyll = jsonNode.get("site");
    return mapper.readValue(jekyll.toString(), SiteConfig.class);
  }


  public <E> E or(E value, E defaultValue) {
    return value != null ? value : defaultValue;
  }


  /**
   * @return relative source path
   */
  public String source() {
    return or(config.getSource(), sourceBase);
  }


  public String destination() {
    return or(config.getDestination(), "_site");
  }


  /**
   * Returns url to project denoting base project style. Defaults to kosher-base-style project on github.
   */
  public String styleRepository() {
    return or(config.getStyleRepository(), "https://github.com/tunguski/kosher-base-style.git");
  }


  /**
   * Returns branch from style repository that should be used.
   */
  public String styleBranch() {
    return or(config.getStyleBranch(), "master");
  }


  /**
   * Returns style directory to which style repository should be checked out.
   */
  public String styleDirectory() {
    return or(config.getStyleDirectory(), ".style");
  }
}

