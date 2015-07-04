package pl.matsuo.gitlab.service.build;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.git.GitRepositoryService;

import java.util.concurrent.CompletableFuture;


/**
 * Created by marek on 04.07.15.
 */
public abstract class PartialBuilder {


  @Autowired
  GitRepositoryService gitRepositoryService;


  @Async
  public final CompletableFuture<PartialBuildInfo> execute(PushEvent pushEvent) {
    try {
      return internalExecute(pushEvent);
    } catch (Exception e) {
      return null;
    }
  }


  public abstract CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent);
}

