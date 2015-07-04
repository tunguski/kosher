package pl.matsuo.gitlab.hook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import static org.junit.Assert.*;


/**
 * Created by marek on 04.07.15.
 */
public class TestPushEvent {


  MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
  ObjectMapper objectMapper = converter.getObjectMapper();


  @Test
  public void testX() throws Exception {
    PushEvent pushEvent = objectMapper.readValue(
        getClass().getResourceAsStream("/sample_pushEvent.json"), PushEvent.class);

    assertNotNull(pushEvent);
    assertEquals("http://example.com/mike/diaspora.git", pushEvent.getRepository().getGit_http_url());

    String serialized = objectMapper.writeValueAsString(pushEvent);

    assertNotNull(serialized);
    assertEquals(1084, serialized.length());
  }
}

