package pl.matsuo.gitlab.service.build;

import org.junit.Before;
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

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 * Created by marek on 04.07.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestBuildServiceImpl {


  @Mock
  Database database;
  @Mock
  GitRepositoryService gitRepositoryService;
  @Mock
  PartialBuilder partialBuilder;
  @InjectMocks
  BuildServiceImpl buildService = new BuildServiceImpl();


  @Before
  public void beforeTest() {
    reset(partialBuilder);

    buildService.partialBuilder = new ArrayList<>();
    buildService.partialBuilder.add(partialBuilder);
  }


  @Test
  public void testPushEvent() throws Exception {
    PushEvent pushEvent = new PushEvent();
    pushEvent.setRepository(new Repository());
    pushEvent.setRef("refs/heads/master");

    pushEvent.getRepository().setUrl("git@github.com:tunguski/gitlab-java-event-listener.git");
    buildService.pushEvent(pushEvent);

    verify(partialBuilder).execute(any(PushEvent.class));

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

    when(database.get(idBuild, BuildInfo.class)).thenReturn(result);

    BuildInfo buildInfo = buildService.buildStatus(idBuild);

    assertEquals(idBuild, buildInfo.getId());
  }
}

