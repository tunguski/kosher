package pl.matsuo.gitlab.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.hook.Repository;
import pl.matsuo.gitlab.service.build.BuildServiceImpl;
import pl.matsuo.gitlab.service.build.jekyll.JekyllPartialBuilder;
import pl.matsuo.gitlab.service.db.MapDbDatabase;
import pl.matsuo.gitlab.service.git.GitRepositoryServiceImpl;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Created by marek on 04.07.15.
 */
@ContextConfiguration(classes = { BuildServiceImpl.class, MapDbDatabase.class, GitRepositoryServiceImpl.class,
                                  JekyllPartialBuilder.class, WebViewController.class })
public class TestWebViewController extends AbstractControllerRequestTest {


  @Autowired
  BuildServiceImpl buildService;


  @Test
  public void testGetRequest() throws Exception {
    PushEvent pushEvent = new PushEvent();
    pushEvent.setRepository(new Repository());
    pushEvent.setRef("refs/heads/master");
    pushEvent.getRepository().setUrl("https://github.com/tunguski/gitlab-java-event-listener.git");

    buildService.pushEvent(pushEvent);

    performAndCheckStatus(get("/site/tunguski/gitlab-java-event-listener/master/index.html"), status().isOk(),
        html -> System.out.println(html),
        html -> assertTrue(html.contains("<h1>Test!</h1>")));
  }
}

