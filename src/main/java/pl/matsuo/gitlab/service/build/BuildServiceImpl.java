package pl.matsuo.gitlab.service.build;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import pl.matsuo.gitlab.data.BuildInfo;
import pl.matsuo.gitlab.data.ProjectInfo;
import pl.matsuo.gitlab.data.UserInfo;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.db.Database;
import pl.matsuo.gitlab.service.git.GitRepositoryService;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.*;
import static pl.matsuo.gitlab.util.PushEventUtil.*;


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
  List<PartialBuilder> partialBuilders;
  @Autowired(required = false)
  TaskExecutor taskExecutor;


  @PostConstruct
  public void init() {
    // sort builders using @Order annotation - it is not enabled by default
    if (partialBuilders != null) {
      sort(partialBuilders, AnnotationAwareOrderComparator.INSTANCE);
    }
  }


  public String pushEvent(@RequestBody PushEvent pushEvent) {
    String user = getUser(pushEvent);
    String project = getRepository(pushEvent);
    String branch = getRef(pushEvent);

    // add userInfo if not exist, add project to userInfo
    db.update(user, UserInfo.class, userInfo -> {
      if (userInfo == null) {
        userInfo = new UserInfo();
      }
      userInfo.getProjects().add(project);
      return userInfo;
    });

    // add projectInfo if not exist, add branch to projectInfo
    db.update(subPath(user, project), ProjectInfo.class, projectInfo -> {
      if (projectInfo == null) {
        projectInfo = new ProjectInfo();
      }
      projectInfo.getBranches().add(branch);
      return projectInfo;
    });

    String idBuild = commit(pushEvent);
    // reference for branch to commit
    db.put(subPath(pushEvent), idBuild);

    {
      BuildInfo buildInfo = new BuildInfo();
      buildInfo.setId(idBuild);
      buildInfo.setPushEvent(pushEvent);
      db.put(idBuild, buildInfo);
    }

    Runnable exec = () -> {
      gitRepositoryService.checkout(pushEvent);

      if (partialBuilders != null) {
        gitRepositoryService.getKosher(pushEvent).ifPresent(config -> {
          Properties properties = new Properties();
          try {
            properties.load(new FileInputStream(config));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }

          CompletableFuture<PartialBuildInfo> future = new CompletableFuture<>();
          future.complete(null);

          // add all partial builders in async queue
          for (PartialBuilder builder : partialBuilders) {
            if (builder.shouldExecute(pushEvent, properties)) {
              future = future.thenCompose(info -> {
                if (info != null) {
                  BuildInfo buildInfo = db.get(idBuild, BuildInfo.class);
                  buildInfo.getPartialStatuses().put(info.getName(), info);
                  db.put(idBuild, buildInfo);
                }

                return builder.execute(pushEvent, properties);
              });
            }
          }

          // after all partial builders finished, add data to database
          future.thenCompose(info -> {
            if (info != null) {
              BuildInfo buildInfo = db.get(idBuild, BuildInfo.class);
              buildInfo.getPartialStatuses().put(info.getName(), info);
              db.put(idBuild, buildInfo);

              System.out.println("Build finished: " + idBuild);
            }

            return null;
          });
        });
      }
    };

    // if taskExecutor is not configured, execute in synch
    if (taskExecutor != null) {
      taskExecutor.execute(exec);
    } else {
      exec.run();
    }

    return idBuild;
  }


  public BuildInfo buildStatus(String idBuild) {
    return db.get(idBuild, BuildInfo.class);
  }
}

