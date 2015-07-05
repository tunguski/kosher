package pl.matsuo.gitlab.service.build.docs;

import org.junit.Test;
import org.mapdb.Fun;
import org.springframework.test.context.ContextConfiguration;
import pl.matsuo.gitlab.service.build.AbstractPartialBuildTest;

import java.io.File;


/**
 * Created by marek on 05.07.15.
 */
@ContextConfiguration(classes = { DocumentationStylePartialBuilder.class })
public class TestDocumentationStylePartialBuilder extends AbstractPartialBuildTest {

  @Test
  public void testInternalExecute() throws Exception {
    Fun.Tuple2<String, File> checkout = checkoutProject();

    // fixme: check generated report
  }
}

