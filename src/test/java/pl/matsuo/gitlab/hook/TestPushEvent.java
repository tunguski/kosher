package pl.matsuo.gitlab.hook;

import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/** Created by marek on 04.07.15. */
public class TestPushEvent {

  MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
  ObjectMapper objectMapper = converter.getObjectMapper();

  @Test
  public void testX() throws Exception {
    PushEvent pushEvent =
        objectMapper.readValue(
            getClass().getResourceAsStream("/sample_pushEvent.json"), PushEvent.class);

    assertNotNull(pushEvent);
    assertEquals(
        "http://github.com/tunguski/kosher.git", pushEvent.getRepository().getGit_http_url());

    String serialized = objectMapper.writeValueAsString(pushEvent);

    assertNotNull(serialized);
    assertEquals(1083, serialized.length());
  }
}
