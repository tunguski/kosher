package pl.matsuo.gitlab.service.build.findbugs;

import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.build.CommandExecutingPartialBuilder;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;


/**
 * Created by marek on 05.07.15.
 */
@Service
public class FindBugsPartialBuilder extends CommandExecutingPartialBuilder {


  @Override
  public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, Properties properties) {
    return internalExecute(pushEvent, ".", "target",
        // fixme: compilation shuld be enabled after checkout?
        destination -> new String[] { "mvn", "compile", "findbugs:findbugs" },
        executeWithReport(pushEvent, "findbugsXml.xml", (partialBuildInfo, generationBase, reportBody) -> {
          // fixme: parse report and calculate all quality values

          return reportBody;
        }));
  }


  @Override
  public String getName() {
    return "findbugs";
  }


  @Override
  public boolean shouldExecute(PushEvent pushEvent, Properties properties) {
    boolean exists = new File(gitRepositoryService.repository(pushEvent), "pom.xml").exists();
    System.out.println(getName() + " partial builder should execute: " + exists);
    return exists;
  }
}

