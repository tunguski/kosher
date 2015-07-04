package pl.matsuo.gitlab.service.build.jekyll;

import org.junit.Test;
import pl.matsuo.gitlab.hook.PushEvent;

import java.util.Properties;

import static org.junit.Assert.*;


/**
 * Created by marek on 04.07.15.
 */
public class TestJekyllPartialBuilder {


  JekyllPartialBuilder jekyllPartialBuilder = new JekyllPartialBuilder();


  @Test
  public void testInternalExecute() throws Exception {
    PushEvent pushEvent = new PushEvent();

    jekyllPartialBuilder.execute(pushEvent, new Properties());
  }
}

