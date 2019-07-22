package pl.matsuo.gitlab.service.build.pmd;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import pl.matsuo.gitlab.service.build.AbstractSingleReportBuilderTest;

/** Created by marek on 05.07.15. */
@ContextConfiguration(classes = {PmdPartialBuilder.class})
public class TestPmdPartialBuilder extends AbstractSingleReportBuilderTest {

  @Test
  public void testInternalExecute() throws Exception {
    testInternalExecute("target/pmd.xml", "pmd");
  }
}
