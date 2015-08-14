package pl.matsuo.gitlab.service.build;

import org.junit.runner.RunWith;
import org.mapdb.Fun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import pl.matsuo.gitlab.AbstractSampleProjectTest;
import pl.matsuo.gitlab.conf.TestConfig;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.hook.Repository;
import pl.matsuo.gitlab.service.db.Database;
import pl.matsuo.gitlab.service.db.MapDbDatabase;
import pl.matsuo.gitlab.service.git.GitRepositoryService;
import pl.matsuo.gitlab.service.git.GitRepositoryServiceImpl;

import java.io.File;


/**
 * Created by marek on 05.07.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestConfig.class, MapDbDatabase.class, BuildServiceImpl.class, GitRepositoryServiceImpl.class})
public abstract class AbstractPartialBuildTest extends AbstractSampleProjectTest {


  @Autowired
  protected BuildService buildService;
  @Autowired
  protected GitRepositoryService gitRepositoryService;
  @Autowired
  protected Database db;


  protected Fun.Tuple2<String, File> checkoutProject() {
    PushEvent pushEvent = new PushEvent();
    pushEvent.setRepository(new Repository());
    pushEvent.getRepository().setUrl("ssh://github.com/tunguski/gitlab-java-event-listener.git");
    pushEvent.getRepository().setGit_http_url("http://github.com/tunguski/gitlab-java-event-listener.git");
    pushEvent.getRepository().setGit_ssh_url("git@github.com:tunguski/gitlab-java-event-listener.git");
    pushEvent.setRef("refs/heads/master");

    String idBuild = buildService.pushEvent(pushEvent);
    File repository = gitRepositoryService.repository(pushEvent);

    return new Fun.Tuple2(idBuild, repository);
  }
}

