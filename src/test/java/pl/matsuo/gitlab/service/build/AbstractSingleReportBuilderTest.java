package pl.matsuo.gitlab.service.build;

import org.mapdb.Fun;
import pl.matsuo.gitlab.hook.BuildInfo;

import java.io.File;

import static org.junit.Assert.*;


/**
 * Created by marek on 07.07.15.
 */
public abstract class AbstractSingleReportBuilderTest extends AbstractPartialBuildTest {


  public void testInternalExecute(String reportPath, String builderName) throws Exception {
    Fun.Tuple2<String, File> checkout = checkoutProject();

    assertTrue(new File(checkout.b, reportPath).exists());

    BuildInfo buildInfo = db.get(checkout.a, BuildInfo.class);

    assertEquals(1, buildInfo.getPartialStatuses().size());
    assertEquals("ok", buildInfo.getPartialStatuses().get(builderName).getStatus());

    assertNotNull(db.get(buildInfo.getPartialStatuses().get(builderName).getIdReport(), String.class));
  }
}

