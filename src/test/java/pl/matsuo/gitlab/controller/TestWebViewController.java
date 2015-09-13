package pl.matsuo.gitlab.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
    pushEvent.getRepository().setUrl("ssh://github.com/tunguski/kosher.git");
    pushEvent.getRepository().setGit_http_url("http://github.com/tunguski/kosher.git");
    pushEvent.getRepository().setGit_ssh_url("git@github.com:tunguski/kosher.git");
    pushEvent.setRef("refs/heads/master");
    pushEvent.setAfter("78af4d73667e3ef4bbb06e82270e0015a1f251ea");

    buildService.pushEvent(pushEvent);

    performAndCheckStatus(get("/tunguski/kosher/master/index.html"), status().isOk(),
        html -> System.out.println("----\n" + html + "\n----"),
        html -> assertTrue(html.contains("<h1>Test!</h1>")));

    performAndCheckStatus(get("/tunguski/kosher/master/markdown.html"), status().isOk(),
        html -> System.out.println("----\n" + html + "\n----"),
        // link relativeness
        html -> assertTrue(html.contains("<link rel=\"stylesheet\" type=\"text/css\" href=\"./css/main.css\">")),
        // link relativeness
        html -> assertTrue(html.contains("window.base_mustache = './';")),
        // lists and bold
        html -> assertTrue(html.contains("<li><strong>two</strong></li>")),
        // headers
        html -> assertTrue(html.contains("<h2>Complex table with strong and italic</h2>")),
        // conditional parts
        html -> assertTrue(html.contains("<div class=\"col-sm-12 col-md-4 col-lg-4 page-menu\">")),
        // conditional parts
        html -> assertTrue(html.contains("<div class=\"col-sm-12 col-md-8 col-lg-8 project-description\"")),
        // tables
        html -> assertTrue(html.contains("<th>col3</th>")),
        // minus sign in table
        html -> assertTrue(html.contains("<td>- </td>")),
        // links
        html -> assertTrue(html.contains("<a href=\"tunguski.github.io\">Some link</a>"))
    );

    performAndCheckStatus(get("/tunguski/kosher/master/branch_master.html"), status().isOk(),
        html -> System.out.println("----\n" + html + "\n----"),
        html -> assertTrue(html.contains("tunguski - kosher - master")));
  }
}

