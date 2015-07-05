package pl.matsuo.gitlab.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.hook.Repository;
import pl.matsuo.gitlab.service.build.BuildServiceImpl;
import pl.matsuo.gitlab.service.build.jekyll.JekyllPartialBuilder;
import pl.matsuo.gitlab.service.db.MapDbDatabase;
import pl.matsuo.gitlab.service.git.GitRepositoryServiceImpl;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by marek on 05.07.15.
 */
@ContextConfiguration(classes = { BuildServiceImpl.class, MapDbDatabase.class, GitRepositoryServiceImpl.class,
                                  JekyllPartialBuilder.class, CodeStatisticsController.class })
public class TestCodeStatisticsController extends AbstractControllerRequestTest {


  @Autowired
  BuildServiceImpl buildService;


  @Before
  public void before() {
    PushEvent pushEvent = new PushEvent();
    pushEvent.setRepository(new Repository());
    pushEvent.setRef("refs/heads/master");
    pushEvent.getRepository().setUrl("https://github.com/tunguski/gitlab-java-event-listener.git");

    buildService.pushEvent(pushEvent);
  }


  @Test
  public void testCheckstyle() throws Exception {
    performAndCheckStatus(get("/tunguski/gitlab-java-event-listener/master/checkstyle"), status().isOk(),
        html -> assertTrue(html.contains("checkstyle")));
  }


  @Test
  public void testFindbugs() throws Exception {
    performAndCheckStatus(get("/tunguski/gitlab-java-event-listener/master/findbugs"), status().isOk(),
        html -> assertTrue(html.contains("findbugs")));
  }


  @Test
  public void testJavancss() throws Exception {
    performAndCheckStatus(get("/tunguski/gitlab-java-event-listener/master/javancss"), status().isOk(),
        html -> assertTrue(html.contains("javancss")));
  }


  @Test
  public void testPmd() throws Exception {
    performAndCheckStatus(get("/tunguski/gitlab-java-event-listener/master/pmd"), status().isOk(),
        html -> assertTrue(html.contains("pmd")));
  }
}

