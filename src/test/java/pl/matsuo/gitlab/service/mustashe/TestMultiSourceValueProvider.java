package pl.matsuo.gitlab.service.mustashe;

import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.util.Map;
import org.junit.Test;

/** Created by marek on 13.09.15. */
public class TestMultiSourceValueProvider {

  ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  @Test
  public void testGet() throws Exception {
    MultiSourceValueProvider provider =
        new MultiSourceValueProvider(mapper.readTree("project:\n  test: \"value\""));
    String test = provider.get(js -> js.get("test") != null ? js.get("test").asText() : null);
    assertNull(test);
    test = provider.get(js -> js.get("project").get("test").asText());
    assertEquals("value", test);
  }

  @Test
  public void testAdd() throws Exception {
    MultiSourceValueProvider provider = new MultiSourceValueProvider();
    provider.add(mapper.readTree("project:\n  test: \"value\""));

    assertEquals("value", provider.asMap().get("project.test"));
  }

  @Test
  public void testAsMap() throws Exception {
    MultiSourceValueProvider provider =
        new MultiSourceValueProvider(mapper.readTree("project:\n  test: \"value\""));
    Map<String, Object> map = provider.asMap();
    assertNull(map.get("test"));
    assertEquals("value", map.get("project.test"));
  }
}
