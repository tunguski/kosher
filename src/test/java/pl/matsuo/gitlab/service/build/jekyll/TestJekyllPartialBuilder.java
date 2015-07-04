package pl.matsuo.gitlab.service.build.jekyll;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import pl.matsuo.gitlab.conf.TestConfig;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.hook.Repository;
import pl.matsuo.gitlab.service.build.BuildService;
import pl.matsuo.gitlab.service.build.BuildServiceImpl;
import pl.matsuo.gitlab.service.db.MapDbDatabase;
import pl.matsuo.gitlab.service.git.GitRepositoryServiceImpl;


/**
 * Created by marek on 04.07.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestConfig.class, MapDbDatabase.class, BuildServiceImpl.class,
    GitRepositoryServiceImpl.class, JekyllPartialBuilder.class })
public class TestJekyllPartialBuilder {


  @Autowired
  BuildService buildService;


  @Test
  public void testInternalExecute() throws Exception {
    PushEvent pushEvent = new PushEvent();
    pushEvent.setRepository(new Repository());
    pushEvent.getRepository().setUrl("http://github.com/tunguski/gitlab-java-event-listener.git");
    pushEvent.setRef("refs/heads/master");

    buildService.pushEvent(pushEvent);
  }
}

