package pl.matsuo.gitlab.service.build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import pl.matsuo.gitlab.hook.BuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.hook.Repository;
import pl.matsuo.gitlab.service.build.BuildServiceImpl;
import pl.matsuo.gitlab.service.db.Database;
import pl.matsuo.gitlab.service.git.GitRepositoryService;

import static org.junit.Assert.*;


/**
 * Created by marek on 04.07.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestBuildServiceImpl {


  @Mock
  Database database;
  @Mock
  GitRepositoryService gitRepositoryService;
  @InjectMocks
  BuildServiceImpl buildService = new BuildServiceImpl();


  @Test
  public void testPushEvent() throws Exception {
    PushEvent pushEvent = new PushEvent();
    pushEvent.setRepository(new Repository());
    pushEvent.setRef("refs/heads/master");

    pushEvent.getRepository().setUrl("git@github.com:tunguski/gitlab-java-event-listener.git");
    buildService.pushEvent(pushEvent);

    pushEvent.getRepository().setUrl("http://github.com/tunguski/gitlab-java-event-listener.git");
    buildService.pushEvent(pushEvent);
  }


  @Test
  public void testBuildStatus() throws Exception {
    PushEvent pushEvent = new PushEvent();
    pushEvent.setRepository(new Repository());
    pushEvent.setRef("refs/heads/master");
    pushEvent.getRepository().setUrl("git@github.com:tunguski/gitlab-java-event-listener.git");

    String idBuild = buildService.pushEvent(pushEvent);

    BuildInfo result = new BuildInfo();
    result.setId(idBuild);

    Mockito.when(database.get(idBuild, BuildInfo.class)).thenReturn(result);

    BuildInfo buildInfo = buildService.buildStatus(idBuild);

    assertEquals(idBuild, buildInfo.getId());
  }
}

