package pl.matsuo.gitlab.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Created by marek on 04.07.15.
 */
@ContextConfiguration(classes = { WebViewController.class })
public class TestWebViewController extends AbstractControllerRequestTest {


  @Test
  public void testGetRequest() throws Exception {
    performAndCheckStatus(get("/site/tunguski/gitlab-java-event-listener/master/index.html"), status().isOk(),
        html -> System.out.println(html),
        html -> assertTrue(html.contains("<h1>Test!</h1>")));
  }
}

