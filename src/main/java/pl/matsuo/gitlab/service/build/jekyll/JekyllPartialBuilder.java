package pl.matsuo.gitlab.service.build.jekyll;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.build.CommandExecutingPartialBuilder;
import pl.matsuo.gitlab.service.execute.ExecutionService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;


/**
 * Created by marek on 04.07.15.
 */
@Service @Order(Ordered.HIGHEST_PRECEDENCE)
public class JekyllPartialBuilder extends CommandExecutingPartialBuilder {


  @Autowired
  ExecutionService executionService;


  public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, File config) {
    JekyllProperties jekyllProperties = new JekyllProperties(config, true);

    PartialBuildInfo partialBuildInfo = new PartialBuildInfo();
    partialBuildInfo.setName(getName());
    partialBuildInfo.setStatus("ok");

    CompletableFuture<PartialBuildInfo> future = new CompletableFuture();
    File projectBase = gitRepositoryService.repository(pushEvent);

    File generationBase = new File(projectBase, jekyllProperties.destination());
    if (generationBase.exists()) {
      try {
        FileUtils.deleteDirectory(generationBase);
      } catch (IOException e) {
        e.printStackTrace();
        partialBuildInfo.setStatus(e.getMessage());
      }
    }

    if (!generationBase.mkdirs()) {
      try {
        // FileUtils is better at creating directories than JRE
        FileUtils.forceMkdir(generationBase);
      } catch (IOException e) {
        e.printStackTrace();
        partialBuildInfo.setStatus(e.getMessage());
      }
    }

    executionService.run(() -> {
      File styleDirectory = new File(projectBase, jekyllProperties.styleDirectory());
      if (styleDirectory.exists()) {
        try {
          FileUtils.deleteDirectory(styleDirectory);
        } catch (IOException e) {
          e.printStackTrace();
          partialBuildInfo.setStatus(e.getMessage());
        }
      }

      if (!styleDirectory.mkdirs()) {
        try {
          // FileUtils is better at creating directories than JRE
          FileUtils.forceMkdir(styleDirectory);
        } catch (IOException e) {
          e.printStackTrace();
          partialBuildInfo.setStatus(e.getMessage());
        }
      }

      try {
        Git.cloneRepository()
            .setBranch(jekyllProperties.styleBranch())
            .setURI(jekyllProperties.styleRepository())
            .setDirectory(styleDirectory)
            .call();
      } catch (Exception e) {
        e.printStackTrace();
        partialBuildInfo.setStatus(e.getMessage());
      }
    });

    future.complete(partialBuildInfo);

    return future;
  }


  @Override
  public boolean shouldExecute(PushEvent pushEvent, File properties) {
    return true;
  }
}

