package pl.matsuo.gitlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import pl.matsuo.gitlab.conf.TestConfig;
import pl.matsuo.gitlab.data.BuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.build.BuildServiceImpl;
import pl.matsuo.gitlab.service.db.Database;
import pl.matsuo.gitlab.service.db.MapDbDatabase;
import pl.matsuo.gitlab.service.execute.ExecutionServiceImpl;
import pl.matsuo.gitlab.service.git.GitRepositoryServiceImpl;

import java.io.File;

import static org.apache.commons.io.FileUtils.*;
import static org.junit.Assert.*;
import static pl.matsuo.gitlab.data.BuildStatus.*;


/**
 * Created by marek on 04.07.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestConfig.class, MapDbDatabase.class, BuildServiceImpl.class, ExecutionServiceImpl.class,
                                  GitRepositoryServiceImpl.class, GitLabWebHookController.class })
public class TestGitLabWebHookController {


  @Autowired
  Database db;
  @Autowired
  GitLabWebHookController gitLabWebHookController;
  @Value("${repositoryBase}")
  String repositoryBase;


  MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
  ObjectMapper objectMapper = converter.getObjectMapper();


  @Test
  public void testPushEvent() throws Exception {
    deleteDirectory(new File(repositoryBase));

    PushEvent pushEvent = objectMapper.readValue(
        getClass().getResourceAsStream("/sample_pushEvent.json"), PushEvent.class);
    String idBuild = gitLabWebHookController.pushEvent(pushEvent);

    assertFalse(db.isEmpty());

    BuildInfo buildInfo = db.get(idBuild, BuildInfo.class);

    assertEquals(Pending, buildInfo.getStatus());
    assertEquals(idBuild, buildInfo.getId());
  }


  @Test
  public void testBuildStatus() throws Exception {
    PushEvent pushEvent = objectMapper.readValue(
        getClass().getResourceAsStream("/sample_pushEvent.json"), PushEvent.class);
    String idBuild = gitLabWebHookController.pushEvent(pushEvent);

    BuildInfo buildInfo = gitLabWebHookController.buildStatus(idBuild);

    assertEquals(Pending, buildInfo.getStatus());
    assertEquals(idBuild, buildInfo.getId());
  }
}

