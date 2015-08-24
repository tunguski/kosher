package pl.matsuo.gitlab.service.build.pmd;

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
public class PmdPartialBuilder extends CommandExecutingPartialBuilder {


  @Override
  public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, Properties properties) {
    return internalExecute(pushEvent, ".", "target",
        destination -> new String[] { "mvn", "pmd:pmd" },
        executeWithReport("pmd.xml", (partialBuildInfo, generationBase, reportBody) -> {
          // fixme: parse report and calculate all quality values

          return reportBody;
        }));
  }


  @Override
  public String getName() {
    return "pmd";
  }


  @Override
  public boolean shouldExecute(PushEvent pushEvent, Properties properties) {
    boolean exists = new File(gitRepositoryService.repository(pushEvent), "pom.xml").exists();
    System.out.println(getName() + " partial builder should execute: " + exists);
    return exists;
  }
}

