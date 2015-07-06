package pl.matsuo.gitlab.service.build.checkstyle;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import pl.matsuo.gitlab.service.build.AbstractSingleReportBuilderTest;


/**
 * Created by marek on 05.07.15.
 */
@ContextConfiguration(classes = { CheckStylePartialBuilder.class })
public class TestCheckStylePartialBuilder extends AbstractSingleReportBuilderTest {


  @Test
  public void testInternalExecute() throws Exception {
    testInternalExecute("target/checkstyle-result.xml", "checkstyle");
  }
}

