package pl.matsuo.gitlab.service.build.javancss;

import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.build.CommandExecutingPartialBuilder;

import java.io.File;
import java.util.concurrent.CompletableFuture;


/**
 * Created by marek on 05.07.15.
 */
@Service
public class JavaNcssPartialBuilder extends CommandExecutingPartialBuilder {


  @Override
  public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, File properties) {
    return internalExecute(pushEvent, ".", "target",
        destination -> new String[] { "mvn", "javancss:report" },
        (partialBuildInfo, generationBase) -> {});
  }


  @Override
  public boolean shouldExecute(PushEvent pushEvent, File properties) {
    boolean exists = new File(gitRepositoryService.repository(pushEvent), "pom.xml").exists();
    System.out.println(getName() + " partial builder should execute: " + exists);
    return exists;
  }
}

