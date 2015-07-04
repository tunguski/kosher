package pl.matsuo.gitlab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import pl.matsuo.gitlab.hook.BuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.hook.Repository;

import java.util.List;


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

    // fixme: execute partial builds

    return idBuild;
  }


  public BuildInfo buildStatus(String idBuild) {
    return db.get(idBuild, BuildInfo.class);
  }
}

