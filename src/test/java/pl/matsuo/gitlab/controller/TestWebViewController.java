package pl.matsuo.gitlab.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


/**
 * Created by marek on 04.07.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebViewController.class })
public class TestWebViewController {


  @Autowired
  WebViewController controller;


  @Test
  public void testGet() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("test_request");

    String response = controller.get(request);
    Assert.assertEquals("test_request", response);
  }
}

