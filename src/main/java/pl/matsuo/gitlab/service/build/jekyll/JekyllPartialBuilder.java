package pl.matsuo.gitlab.service.build.jekyll;

import static org.apache.commons.io.FileUtils.*;
import static pl.matsuo.gitlab.function.FunctionalUtil.*;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.function.ThrowingExceptionsRunnable;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.build.CommandExecutingPartialBuilder;
import pl.matsuo.gitlab.service.execute.ExecutionService;

/** Created by marek on 04.07.15. */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JekyllPartialBuilder extends CommandExecutingPartialBuilder {

  @Autowired ExecutionService executionService;

  public void execWithLogException(
      boolean shouldExecute,
      PartialBuildInfo partialBuildInfo,
      ThrowingExceptionsRunnable runnable) {
    if (shouldExecute) {
      runtimeEx(
          runnable,
          e -> {
            e.printStackTrace();
            partialBuildInfo.setStatus(e.getMessage());
          });
    }
  }

  public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, File config) {
    JekyllProperties jekyllProperties = new JekyllProperties(config);

    PartialBuildInfo partialBuildInfo = new PartialBuildInfo();
    partialBuildInfo.setName(getName());
    partialBuildInfo.setStatus("ok");

    CompletableFuture<PartialBuildInfo> future = new CompletableFuture();
    File projectBase = gitRepositoryService.repository(pushEvent);

    File generationBase = new File(projectBase, jekyllProperties.destination());
    execWithLogException(
        generationBase.exists(), partialBuildInfo, () -> deleteDirectory(generationBase));

    // FileUtils is better at creating directories than JRE
    execWithLogException(
        !generationBase.mkdirs(), partialBuildInfo, () -> forceMkdir(generationBase));

    executionService.run(
        () -> {
          File styleDirectory = new File(projectBase, jekyllProperties.styleDirectory());
          execWithLogException(
              styleDirectory.exists(), partialBuildInfo, () -> deleteDirectory(styleDirectory));

          // FileUtils is better at creating directories than JRE
          execWithLogException(
              !styleDirectory.mkdirs(), partialBuildInfo, () -> forceMkdir(styleDirectory));

          execWithLogException(
              true,
              partialBuildInfo,
              () ->
                  Git.cloneRepository()
                      .setBranch(jekyllProperties.styleBranch())
                      .setURI(jekyllProperties.styleRepository())
                      .setDirectory(styleDirectory)
                      .call());
        });

    future.complete(partialBuildInfo);

    return future;
  }

  @Override
  public boolean shouldExecute(PushEvent pushEvent, File properties) {
    return true;
  }
}
