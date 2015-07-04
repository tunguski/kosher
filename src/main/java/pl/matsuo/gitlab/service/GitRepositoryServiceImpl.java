package pl.matsuo.gitlab.service;

import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.exception.GitException;

import java.io.File;


/**
 * Created by marek on 04.07.15.
 */
@Service
public class GitRepositoryServiceImpl implements GitRepositoryService {


  @Value("${repositoryBase}")
  String repositoryBase;


  protected File getRepositoryFile(String userName, String projectName, String refName) {
    return new File(repositoryBase, userName + "/" + projectName + "/" + refName);
  }


  public Git checkout(String userName, String projectName, String refName, String uri) {
    try {
      File cloneFile = getRepositoryFile(userName, projectName, refName);

      Git git;
      if (!cloneFile.exists()) {
        git = Git.cloneRepository()
            .setBranch("master")
            .setURI(uri)
            .setDirectory(cloneFile)
            .call();
      } else {
        git = Git.open(cloneFile);
        git.checkout().setName(refName).setForce(true).call();
      }

      return git;
    } catch (Exception e) {
      throw new GitException(e);
    }
  }
}
