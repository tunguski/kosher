package pl.matsuo.gitlab.controller;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import org.junit.Test;

public class TestGitlabProxyController {

  @Test
  public void sendGet() {
    GitlabProxyController controller =
        new GitlabProxyController() {
          @Override
          protected void setToken(HttpURLConnection con) {
            // do not try to set private token in test
          }
        };
    String result = controller.sendGet("https://gitlab.com");
    assertNotNull(result);
  }
}
