package pl.matsuo.gitlab.service.build.findbugs;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import pl.matsuo.gitlab.service.build.AbstractSingleReportBuilderTest;


/**
 * Created by marek on 05.07.15.
 */
@ContextConfiguration(classes = { FindBugsPartialBuilder.class })
public class TestFindBugsPartialBuilder extends AbstractSingleReportBuilderTest {


  @Test
  public void testInternalExecute() throws Exception {
    testInternalExecute("target/findbugsXml.xml", "findbugs");
  }
}

