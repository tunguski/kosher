package pl.matsuo.gitlab.service.git;

import static org.junit.Assert.*;

import java.io.File;
import org.eclipse.jgit.api.Git;
import org.junit.Test;

public class TestGitRepositoryServiceImpl {

  @Test
  public void onJavaGitCloneFail() {
    GitRepositoryServiceImpl gitRepositoryService =
        new GitRepositoryServiceImpl() {
          @Override
          protected ProcessBuilder executeCloneProcess(String refName, String uri, File cloneFile) {
            return new ProcessBuilder("true");
          }
        };

    Git git =
        gitRepositoryService.onJavaGitCloneFail(
            "tonny", "project", "some_ref", "none", new File(".git"));

    assertNotNull(git);
  }
}
