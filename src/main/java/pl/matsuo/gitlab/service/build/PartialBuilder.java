package pl.matsuo.gitlab.service.build;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.db.Database;
import pl.matsuo.gitlab.service.git.GitRepositoryService;

import java.io.File;
import java.util.concurrent.CompletableFuture;


/**
 * Created by marek on 04.07.15.
 */
public abstract class PartialBuilder {


  @Autowired
  protected GitRepositoryService gitRepositoryService;
  @Autowired
  protected Database database;


  @Async
  public final CompletableFuture<PartialBuildInfo> execute(PushEvent pushEvent, File properties) {
    try {
      return internalExecute(pushEvent, properties);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }


  public abstract CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, File properties);


  public final String getName() {
    return getClass().getSimpleName().substring(0,
        getClass().getSimpleName().length() - "PartialBuilder".length()).toLowerCase();
  }


  public abstract boolean shouldExecute(PushEvent pushEvent, File properties);
}

