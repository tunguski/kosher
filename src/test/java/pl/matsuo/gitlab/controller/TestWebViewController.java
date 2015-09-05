package pl.matsuo.gitlab.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import pl.matsuo.gitlab.file.YamlFileConverterProvider;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.hook.Repository;
import pl.matsuo.gitlab.service.build.BuildServiceImpl;
import pl.matsuo.gitlab.service.build.jekyll.JekyllPartialBuilder;
import pl.matsuo.gitlab.service.db.MapDbDatabase;
import pl.matsuo.gitlab.service.git.GitRepositoryServiceImpl;
import pl.matsuo.gitlab.service.mustashe.GenerateContentServiceImpl;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Created by marek on 04.07.15.
 */
@ContextConfiguration(classes = { BuildServiceImpl.class, MapDbDatabase.class, GitRepositoryServiceImpl.class,
                                  JekyllPartialBuilder.class, YamlFileConverterProvider.class, WebViewController.class,
                                  GenerateContentServiceImpl.class})
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
    pushEvent.setAfter("51ede2a78d50473b548f7992b977f43590b3afbb");
    pushEvent.getRepository().setUrl("https://github.com/tunguski/kosher.git");
    pushEvent.getRepository().setGit_ssh_url("git@github.com:tunguski/kosher.git");

    buildService.pushEvent(pushEvent);

    performAndCheckStatus(get("/tunguski/kosher/master/index.html"), status().isOk(),
        html -> System.out.println("----\n" + html + "\n----"),
        html -> assertTrue(html.contains("<h1>Test!</h1>")));

    performAndCheckStatus(get("/tunguski/kosher/master/markdown.html"), status().isOk(),
        html -> System.out.println("----\n" + html + "\n----"),
        html -> assertTrue(html.contains("<h1>Test!</h1>")));

    performAndCheckStatus(get("/tunguski/kosher/master/branch_master.html"), status().isOk(),
        html -> System.out.println("----\n" + html + "\n----"),
        html -> assertTrue(html.contains("tunguski - kosher - master")));
  }
}

