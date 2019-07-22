package pl.matsuo.gitlab.service.build.javancss;

import java.io.File;
import org.junit.Test;
import org.mapdb.Fun;
import org.springframework.test.context.ContextConfiguration;
import pl.matsuo.gitlab.service.build.AbstractPartialBuildTest;

/** Created by marek on 05.07.15. */
@ContextConfiguration(classes = {JavaNcssPartialBuilder.class})
public class TestJavaNcssPartialBuilder extends AbstractPartialBuildTest {

  @Test
  public void testInternalExecute() throws Exception {
    Fun.Tuple2<String, File> checkout = checkoutProject();

    // fixme:
    //    assertTrue(new File(checkout.b, "target/javancss-result.xml").exists());
    //
    //    BuildInfo buildInfo = db.get(checkout.a, BuildInfo.class);
    //
    //    assertEquals(1, buildInfo.getPartialStatuses().size());
    //    assertEquals("ok", buildInfo.getPartialStatuses().get("javancss").getStatus());
  }
}
