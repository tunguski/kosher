package pl.matsuo.gitlab.service.build;

import org.junit.Test;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

/**
 * Created by marek on 29.08.15.
 */
public class TestPartialBuilder {


  class SamplePartialBuilder extends PartialBuilder {

    @Override
    public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, File properties) {
      return null;
    }

    @Override
    public boolean shouldExecute(PushEvent pushEvent, File properties) {
      return false;
    }
  }


  @Test
  public void testGetName() throws Exception {
    assertEquals("sample", new SamplePartialBuilder().getName());
  }
}

