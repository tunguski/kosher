package pl.matsuo.gitlab.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import pl.matsuo.gitlab.file.YamlFileConverterProvider;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.hook.Repository;
import pl.matsuo.gitlab.service.build.BuildServiceImpl;
import pl.matsuo.gitlab.service.build.jekyll.JekyllGenerateTemplateServiceImpl;
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
                                  JekyllPartialBuilder.class, JekyllGenerateTemplateServiceImpl.class,
                                  YamlFileConverterProvider.class, WebViewController.class })
public class TestWebViewController extends AbstractControllerRequestTest {


  @Autowired
  BuildServiceImpl buildService;
  @Autowired
  WebViewController controller;


  @Test
  public void testGetRequest() throws Exception {
    PushEvent pushEvent = new PushEvent();
    pushEvent.setRepository(new Repository());
    pushEvent.setRef("refs/heads/master");
    pushEvent.setAfter("78af4d73667e3ef4bbb06e82270e0015a1f251ea");
    pushEvent.getRepository().setUrl("https://github.com/tunguski/gitlab-java-event-listener.git");
    pushEvent.getRepository().setGit_ssh_url("git@github.com:tunguski/gitlab-java-event-listener.git");

    buildService.pushEvent(pushEvent);

    performAndCheckStatus(get("/tunguski/gitlab-java-event-listener/master/index.html"), status().isOk(),
        html -> System.out.println(html),
        html -> assertTrue(html.contains("<h1>Test!</h1>")));

    performAndCheckStatus(get("/tunguski/gitlab-java-event-listener/master/branch_master.html"), status().isOk(),
        html -> System.out.println(html),
        html -> assertTrue(html.contains("tunguski - gitlab-java-event-listener - master")),
        html -> assertTrue(html.contains("<base href=\"http://localhost/tunguski/gitlab-java-event-listener/master/\" />")));
  }


  @Test
  public void testGetPageTemplate() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/user/project/branch/directory/file.html");
    String pageTemplate = controller.getPageTemplate("user", "project", "branch", request);

    assertTrue(pageTemplate, pageTemplate.contains("<base href=\"http://localhost/user/project/branch/\" />"));
  }
}

