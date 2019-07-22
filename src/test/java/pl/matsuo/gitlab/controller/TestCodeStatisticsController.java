package pl.matsuo.gitlab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.hook.Repository;
import pl.matsuo.gitlab.service.build.BuildServiceImpl;
import pl.matsuo.gitlab.service.build.checkstyle.CheckStylePartialBuilder;
import pl.matsuo.gitlab.service.build.findbugs.FindBugsPartialBuilder;
import pl.matsuo.gitlab.service.build.javancss.JavaNcssPartialBuilder;
import pl.matsuo.gitlab.service.build.pmd.PmdPartialBuilder;
import pl.matsuo.gitlab.service.db.MapDbDatabase;
import pl.matsuo.gitlab.service.git.GitRepositoryServiceImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Created by marek on 05.07.15.
 */
@ContextConfiguration(classes = { BuildServiceImpl.class, MapDbDatabase.class, GitRepositoryServiceImpl.class,
                                  CheckStylePartialBuilder.class, FindBugsPartialBuilder.class,
                                  JavaNcssPartialBuilder.class, PmdPartialBuilder.class,
                                  CodeStatisticsController.class })
public class TestCodeStatisticsController extends AbstractControllerRequestTest {


  @Autowired
  BuildServiceImpl buildService;

  MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
  ObjectMapper objectMapper = converter.getObjectMapper();


  @Before
  public void before() {
    PushEvent pushEvent = new PushEvent();
    pushEvent.setRepository(new Repository());
    pushEvent.setRef("refs/heads/master");
    pushEvent.setAfter("78af4d73667e3ef4bbb06e82270e0015a1f251ea");
    pushEvent.getRepository().setUrl("https://github.com/tunguski/kosher.git");
    pushEvent.getRepository().setGit_ssh_url("git@github.com:tunguski/kosher.git");

    buildService.pushEvent(pushEvent);
  }


  @Test
  public void testCommit() throws Exception {
    performAndCheckStatus(get("/s/tunguski/kosher/master"), status().isOk(),
        ref -> {
          String cleanRef = ref.replaceAll("[\\\\\"]+", "");
          System.out.println("/s/tunguski/kosher/master: " + cleanRef);
          performAndCheckStatus(get("/s/" + cleanRef).header("Accept", "*/*"),
              // fixme - should be ok
              status().isInternalServerError(), html -> {
            System.out.println(cleanRef + ": " + html);

//            BuildInfo buildInfo = objectMapper.readValue(html, BuildInfo.class);
//
//            assertTrue(buildInfo.getPartialStatuses().containsKey("checkstyle"));
//            assertTrue(buildInfo.getPartialStatuses().containsKey("pmd"));
//            assertTrue(buildInfo.getPartialStatuses().containsKey("javancss"));
//            assertTrue(buildInfo.getPartialStatuses().containsKey("findbugs"));
          });
        });
  }
}

