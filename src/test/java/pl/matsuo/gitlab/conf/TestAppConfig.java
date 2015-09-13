package pl.matsuo.gitlab.conf;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;


/**
 * Created by marek on 13.09.15.
 */
public class TestAppConfig {


  @Test
  public void testPropConfig() throws Exception {
    PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = AppConfig.propConfig();
  }


  @Test
  public void testGetAsyncExecutor() throws Exception {
    AppConfig config = new AppConfig();
    TaskExecutor asyncExecutor = config.getAsyncExecutor();

    AtomicBoolean value = new AtomicBoolean(false);

    asyncExecutor.execute(() -> {
      try {
        Thread.sleep(250);
      } catch (InterruptedException e) {
      }

      value.set(true);
    });

    Assert.assertFalse(value.get());
  }
}

