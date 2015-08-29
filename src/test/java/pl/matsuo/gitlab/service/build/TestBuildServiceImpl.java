package pl.matsuo.gitlab.service.build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.matsuo.gitlab.data.BuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.hook.Repository;
import pl.matsuo.gitlab.service.db.Database;
import pl.matsuo.gitlab.service.git.GitRepositoryService;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;

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

    buildService.partialBuilders = new ArrayList<>();
    buildService.partialBuilders.add(partialBuilder);

    when(partialBuilder.shouldExecute(any(PushEvent.class), any(Properties.class))).thenReturn(true);
  }


  @Test
  public void testPushEvent() throws Exception {
    PushEvent pushEvent = new PushEvent();
    pushEvent.setRepository(new Repository());
    pushEvent.setRef("refs/heads/master");
    pushEvent.setAfter("78af4d73667e3ef4bbb06e82270e0015a1f251ea");

    when(gitRepositoryService.getKosher(any(PushEvent.class))).thenReturn(Optional.of(File.createTempFile(".kosher", "")));

    pushEvent.getRepository().setUrl("git@github.com:tunguski/gitlab-java-event-listener.git");
    buildService.pushEvent(pushEvent);

    verify(partialBuilder).execute(any(PushEvent.class), any(Properties.class));

    when(gitRepositoryService.getKosher(any(PushEvent.class))).thenReturn(Optional.of(File.createTempFile(".kosher", "")));

    pushEvent.getRepository().setUrl("https://github.com/tunguski/gitlab-java-event-listener.git");
    buildService.pushEvent(pushEvent);
  }


  @Test
  public void testBuildStatus() throws Exception {
    PushEvent pushEvent = new PushEvent();
    pushEvent.setRepository(new Repository());
    pushEvent.setRef("refs/heads/master");
    pushEvent.setAfter("78af4d73667e3ef4bbb06e82270e0015a1f251ea");
    pushEvent.getRepository().setUrl("git@github.com:tunguski/gitlab-java-event-listener.git");

    when(gitRepositoryService.getKosher(any(PushEvent.class))).thenReturn(Optional.of(File.createTempFile(".kosher", "")));

    String idBuild = buildService.pushEvent(pushEvent);

    BuildInfo result = new BuildInfo();
    result.setId(idBuild);

    when(database.get(idBuild, BuildInfo.class)).thenReturn(result);

    BuildInfo buildInfo = buildService.buildStatus(idBuild);

    assertEquals(idBuild, buildInfo.getId());
  }
}

