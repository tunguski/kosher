package pl.matsuo.gitlab.service.git;

import org.eclipse.jgit.api.Git;
import pl.matsuo.gitlab.hook.PushEvent;

import java.io.File;
import java.util.Optional;


/**
 * Created by marek on 04.07.15.
 */
public interface GitRepositoryService {


  File repository(PushEvent pushEvent);


  Optional<File> getKosher(PushEvent pushEvent);
  Optional<File> getKosher(String userName, String projectName, String refName);


  Git checkout(PushEvent pushEvent);


  Git checkout(String userName, String projectName, String refName, String uri);
}

