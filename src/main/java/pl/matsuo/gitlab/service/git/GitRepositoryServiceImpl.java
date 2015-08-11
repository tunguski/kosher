package pl.matsuo.gitlab.service.git;

import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.exception.GitException;
import pl.matsuo.gitlab.hook.PushEvent;

import java.io.File;
import java.util.Optional;

import static pl.matsuo.gitlab.util.PushEventUtil.*;


/**
 * Created by marek on 04.07.15.
 */
@Service
public class GitRepositoryServiceImpl implements GitRepositoryService {


  @Value("${repositoryBase}")
  String repositoryBase;
  @Value("${gitlabRepositoryBase}")
  String gitlabRepositoryBase;


  public GitRepositoryServiceImpl() {
    // FIXME:
    SshSessionFactory.setInstance(new JschConfigSessionFactory() {
      public void configure(OpenSshConfig.Host hc, Session session) {
        session.setConfig("StrictHostKeyChecking", "no");
      }
    });
  }


  protected File getRepositoryFile(String userName, String projectName, String refName) {
    return new File(repositoryBase, userName + "/" + projectName + "/" + refName);
  }


  public Optional<File> getKosher(PushEvent pushEvent) {
    return getKosher(getUser(pushEvent), getRepository(pushEvent), getRef(pushEvent));
  }


  public Optional<File> getKosher(String userName, String projectName, String refName) {
    File file = new File(repositoryBase, userName + "/" + projectName + "/" + refName + "/.kosher");
    System.out.println("getKosher: " + file.getAbsolutePath());
    return file.exists() ? Optional.of(file) : Optional.<File>empty();
  }


  @Override
  public File repository(PushEvent pushEvent) {
    return getRepositoryFile(getUser(pushEvent), getRepository(pushEvent), getRef(pushEvent));
  }


  @Override
  public Git checkout(PushEvent pushEvent) {
    return checkout(getUser(pushEvent), getRepository(pushEvent), getRef(pushEvent),
        new File(gitlabRepositoryBase,
            pushEvent.getRepository().getGit_ssh_url().split(":")[1]).getAbsoluteFile().toURI().toString());
  }


  @Override
  public Git checkout(String userName, String projectName, String refName, String uri) {
    System.out.println(uri);
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
        git.pull().call();
      }

      return git;
    } catch (Exception e) {
      throw new GitException(e);
    }
  }
}

