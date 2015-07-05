package pl.matsuo.gitlab.service.build.pmd;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import pl.matsuo.gitlab.conf.TestConfig;
import pl.matsuo.gitlab.hook.BuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.hook.Repository;
import pl.matsuo.gitlab.service.build.BuildService;
import pl.matsuo.gitlab.service.build.BuildServiceImpl;
import pl.matsuo.gitlab.service.build.checkstyle.CheckStylePartialBuilder;
import pl.matsuo.gitlab.service.db.Database;
import pl.matsuo.gitlab.service.db.MapDbDatabase;
import pl.matsuo.gitlab.service.git.GitRepositoryService;
import pl.matsuo.gitlab.service.git.GitRepositoryServiceImpl;

import java.io.File;

import static org.junit.Assert.*;


/**
 * Created by marek on 05.07.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestConfig.class, MapDbDatabase.class, BuildServiceImpl.class,
                                  GitRepositoryServiceImpl.class, PmdPartialBuilder.class })
public class TestPmdPartialBuilder {


  @Autowired
  BuildService buildService;
  @Autowired
  GitRepositoryService gitRepositoryService;
  @Autowired
  Database db;


  @Test
  public void testInternalExecute() throws Exception {
    PushEvent pushEvent = new PushEvent();
    pushEvent.setRepository(new Repository());
    pushEvent.getRepository().setUrl("https://github.com/tunguski/gitlab-java-event-listener.git");
    pushEvent.setRef("refs/heads/master");

    String idBuild = buildService.pushEvent(pushEvent);

    File repository = gitRepositoryService.repository(pushEvent);

    assertTrue(new File(repository, "target/pmd-result.xml").exists());

    BuildInfo buildInfo = db.get(idBuild, BuildInfo.class);

    assertEquals(1, buildInfo.getPartialStatuses().size());
    assertEquals("ok", buildInfo.getPartialStatuses().get("pmd").getStatus());
  }
}

