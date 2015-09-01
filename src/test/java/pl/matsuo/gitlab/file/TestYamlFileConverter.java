package pl.matsuo.gitlab.file;

import org.junit.Test;
import pl.matsuo.gitlab.service.build.jekyll.model.projects.Project;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;


/**
 * Created by marek on 29.08.15.
 */
public class TestYamlFileConverter {


  YamlFileConverterProvider converter = new YamlFileConverterProvider();


  private String projectText = "name: \"Something\"";
  private InputStream projectStream = new ByteArrayInputStream("name: \"Something\"".getBytes());


  @Test
  public void testRead() throws Exception {
    assertEquals("Something", converter.read(projectText, Project.class).getName());
  }


  @Test
  public void testRead1() throws Exception {
    assertEquals("Something", converter.read(projectStream, Project.class).getName());
  }


  @Test
  public void testConverter() throws Exception {
    FileConverter<Project> converter = this.converter.converter(Project.class);

    assertEquals("Something", converter.convert(projectText).getName());
    assertEquals("Something", converter.convert(projectStream).getName());
  }
}

