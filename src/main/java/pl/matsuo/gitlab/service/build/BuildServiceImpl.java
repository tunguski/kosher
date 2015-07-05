package pl.matsuo.gitlab.service.build;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import pl.matsuo.gitlab.hook.BuildInfo;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.db.Database;
import pl.matsuo.gitlab.service.git.GitRepositoryService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;


/**
 * Created by marek on 04.07.15.
 */
@Service
public class BuildServiceImpl implements BuildService {


  @Autowired
  Database db;
  @Autowired
  GitRepositoryService gitRepositoryService;
  @Autowired(required = false)
  List<PartialBuilder> partialBuilder;


  public String pushEvent(@RequestBody PushEvent pushEvent) {
    String idBuild = "build_" + System.currentTimeMillis();

    BuildInfo buildInfo = new BuildInfo();
    buildInfo.setId(idBuild);
    buildInfo.setPushEvent(pushEvent);
    db.put(idBuild, buildInfo);

    gitRepositoryService.checkout(pushEvent);

    if (partialBuilder != null) {
      gitRepositoryService.getKosher(pushEvent).ifPresent(config -> {
        Properties properties = new Properties();
        try {
          properties.load(new FileInputStream(config));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        CompletableFuture<PartialBuildInfo> completableFuture = new CompletableFuture<>();
        completableFuture.complete(null);

        CompletableFuture<PartialBuildInfo> future = completableFuture;
        for (PartialBuilder builder : partialBuilder) {
          future = future.thenCompose(info -> {
            if (info != null) {
              BuildInfo buildInfo1 = db.get(idBuild, BuildInfo.class);
              buildInfo1.getPartialStatuses().put(info.getName(), info);
              db.put(idBuild, buildInfo1);
            }

            return builder.execute(pushEvent, properties);
          });
        }

        future.thenCompose(info -> {
          if (info != null) {
            BuildInfo newInfo = db.get(idBuild, BuildInfo.class);
            newInfo.getPartialStatuses().put(info.getName(), info);
            db.put(idBuild, newInfo);
          }

          return null;
        });
      });
    }

    return idBuild;
  }


  public BuildInfo buildStatus(String idBuild) {
    return db.get(idBuild, BuildInfo.class);
  }
}

