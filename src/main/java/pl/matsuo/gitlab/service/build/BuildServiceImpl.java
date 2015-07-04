package pl.matsuo.gitlab.service.build;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import pl.matsuo.gitlab.hook.BuildInfo;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.hook.Repository;
import pl.matsuo.gitlab.service.db.Database;
import pl.matsuo.gitlab.service.git.GitRepositoryService;

import java.lang.ref.Reference;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;


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
    db.put(idBuild, buildInfo);

    Repository repository = pushEvent.getRepository();
    String url = repository.getUrl();
    if (url.startsWith("git@")) {
      String[] splitted = url.split(":")[1].replaceAll("\\.git", "").split("/");
      String[] ref = pushEvent.getRef().split("/");

      // checkout modified branch
      gitRepositoryService.checkout(splitted[0], splitted[1], ref[ref.length - 1], url);
    } else {
      String[] splitted = url.replaceAll("\\.git", "").split("/");
      String[] ref = pushEvent.getRef().split("/");

      // checkout modified branch
      gitRepositoryService.checkout(
          splitted[splitted.length - 2], splitted[splitted.length - 1], ref[ref.length - 1], url);
    }

    if (partialBuilder != null) {
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

          return builder.execute(pushEvent);
        });
      }
    }

    return idBuild;
  }


  public BuildInfo buildStatus(String idBuild) {
    return db.get(idBuild, BuildInfo.class);
  }
}

