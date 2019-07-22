package pl.matsuo.gitlab.conf;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/** Created by marek on 13.09.15. */
public class TestMvcConfig {

  @Test
  public void testConverter() throws Exception {
    MvcConfig config = new MvcConfig();
    MappingJackson2HttpMessageConverter converter = config.converter();

    assertNotNull(converter.getObjectMapper());
  }
}
