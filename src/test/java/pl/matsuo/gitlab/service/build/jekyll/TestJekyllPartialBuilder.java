package pl.matsuo.gitlab.service.build.jekyll;

import org.junit.Test;
import org.mapdb.Fun;
import org.springframework.test.context.ContextConfiguration;
import pl.matsuo.gitlab.data.BuildInfo;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.service.build.AbstractPartialBuildTest;
import pl.matsuo.gitlab.service.build.PartialBuilder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;


/**
 * Created by marek on 04.07.15.
 */
@ContextConfiguration(classes = { JekyllPartialBuilder.class })
public class TestJekyllPartialBuilder extends AbstractPartialBuildTest {


  @Test
  public void testInternalExecute() throws Exception {
    Fun.Tuple2<String, File> checkout = checkoutProject();

    assertTrue(new File(checkout.b, "_site").exists());
    assertTrue(new File(checkout.b, ".style").exists());

    BuildInfo buildInfo = db.get(checkout.a, BuildInfo.class);

    assertEquals(1, buildInfo.getPartialStatuses().size());
    assertEquals("ok", buildInfo.getPartialStatuses().get("jekyll").getStatus());
  }


  @Test
  public void testExecIO() throws Exception {
    JekyllPartialBuilder builder = new JekyllPartialBuilder();

    PartialBuildInfo buildInfo = new PartialBuildInfo();

    builder.execIO(buildInfo, () -> {});

    assertEquals("Pending", buildInfo.getStatus());
  }


  @Test
  public void testExecIO1() throws Exception {
    JekyllPartialBuilder builder = new JekyllPartialBuilder();

    PartialBuildInfo buildInfo = new PartialBuildInfo();

    builder.execIO(buildInfo, () -> {
      throw new IOException("Test");
    });

    assertEquals("Test", buildInfo.getStatus());
  }
}

