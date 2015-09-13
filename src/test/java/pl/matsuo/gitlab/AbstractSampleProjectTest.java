package pl.matsuo.gitlab;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;


/**
 * Created by marek on 11.08.15.
 */
public abstract class AbstractSampleProjectTest {


  @Value("${gitlabRepositoryBase}")
  String gitlabRepositoryBase;


  @Before
  public void setup() {
    File gitlabRepositoryBaseFile = new File(gitlabRepositoryBase);
    if (!gitlabRepositoryBaseFile.exists()) {
      gitlabRepositoryBaseFile.mkdirs();
    }

    File sampleProject = new File(gitlabRepositoryBaseFile, "tunguski/kosher.git");

    if (!sampleProject.exists()) {
      try {
        Git.cloneRepository()
            .setBranch("master")
            .setURI("https://github.com/tunguski/kosher.git")
            .setBare(true)
            .setDirectory(sampleProject)
            .call();
      } catch (GitAPIException e) {
        e.printStackTrace();
      }
      System.out.println("Cloned test repo to " + sampleProject.getAbsolutePath());
    }
  }
}

