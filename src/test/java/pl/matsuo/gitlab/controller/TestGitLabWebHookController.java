package pl.matsuo.gitlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.Database;
import pl.matsuo.gitlab.service.MapDbDatabase;

import static org.junit.Assert.*;


/**
 * Created by marek on 04.07.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { GitLabWebHookController.class, MapDbDatabase.class })
public class TestGitLabWebHookController {


  @Autowired
  Database db;
  @Autowired
  GitLabWebHookController gitLabWebHookController;


  MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
  ObjectMapper objectMapper = converter.getObjectMapper();


  @Test
  public void testPushEvent() throws Exception {
    PushEvent pushEvent = objectMapper.readValue(getClass().getResourceAsStream("/sample_pushEvent.json"), PushEvent.class);
    gitLabWebHookController.pushEvent(pushEvent);

    assertFalse(db.isEmpty());

    String serialized = db.values().iterator().next();
    assertEquals(1084, serialized.length());
  }
}