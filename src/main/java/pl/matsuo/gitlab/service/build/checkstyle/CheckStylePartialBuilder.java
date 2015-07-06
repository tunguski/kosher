package pl.matsuo.gitlab.service.build.checkstyle;

import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.build.CommandExecutingPartialBuilder;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;


/**
 * Created by marek on 05.07.15.
 */
@Service
public class CheckStylePartialBuilder extends CommandExecutingPartialBuilder {


  @Override
  public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, Properties properties) {
    return internalExecute(pushEvent, ".", "target",
        destination -> new String[] { "mvn", "checkstyle:checkstyle" },
        executeWithReport("checkstyle-result.xml", (partialBuildInfo, generationBase, reportBody) -> {
          // fixme: parse report and calculate all quality values

          return reportBody;
        }));
  }


  @Override
  public String getName() {
    return "checkstyle";
  }
}

