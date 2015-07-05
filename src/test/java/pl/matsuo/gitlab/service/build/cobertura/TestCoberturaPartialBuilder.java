package pl.matsuo.gitlab.service.build.cobertura;

import org.junit.Test;
import org.mapdb.Fun;
import org.springframework.test.context.ContextConfiguration;
import pl.matsuo.gitlab.hook.BuildInfo;
import pl.matsuo.gitlab.service.build.AbstractPartialBuildTest;

import java.io.File;

import static org.junit.Assert.*;


/**
 * Created by marek on 05.07.15.
 */
@ContextConfiguration(classes = { CoberturaPartialBuilder.class })
public class TestCoberturaPartialBuilder extends AbstractPartialBuildTest {


  @Test
  public void testInternalExecute() throws Exception {
    Fun.Tuple2<String, File> checkout = checkoutProject();

    assertTrue(new File(checkout.b, "target/checkstyle-result.xml").exists());
    BuildInfo buildInfo = db.get(checkout.a, BuildInfo.class);
    assertEquals(1, buildInfo.getPartialStatuses().size());
    assertEquals("ok", buildInfo.getPartialStatuses().get("checkstyle").getStatus());
  }
}

