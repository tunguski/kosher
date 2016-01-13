package pl.matsuo.gitlab.service.git;

import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.hook.PushEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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


  protected File getRepositoryFile(String userName, String projectName, String refName) {
    return new File(repositoryBase, userName + "/" + projectName + "/" + refName);
  }


  public Optional<File> getKosher(PushEvent pushEvent) {
    return getKosher(getUser(pushEvent), getRepository(pushEvent), getRef(pushEvent));
  }


  public Optional<File> getKosher(String userName, String projectName, String refName) {
    File file = new File(repositoryBase, userName + "/" + projectName + "/" + refName + "/.kosher.yml");
    System.out.println("getKosher: " + file.getAbsolutePath() + " exists: " + file.exists());
    return file.exists() ? Optional.of(file) : Optional.<File>empty();
  }


  @Override
  public File repository(PushEvent pushEvent) {
    return getRepositoryFile(getUser(pushEvent), getRepository(pushEvent), getRef(pushEvent));
  }


  @Override
  public Git checkout(PushEvent pushEvent) {
    String path;
    try {
      path = new URL(pushEvent.getRepository().getUrl()).getPath();
    } catch (MalformedURLException e) {
      try {
        path = new URL(pushEvent.getRepository().getGit_http_url()).getPath();
      } catch (MalformedURLException e2) {
        System.out.println("Exception while trying to get checkout path");
        e.printStackTrace();
        throw new RuntimeException(e2);
      }
    }

    System.out.println("checkout path: " + path);

    return checkout(getUser(pushEvent), getRepository(pushEvent), getRef(pushEvent),
        new File(gitlabRepositoryBase, path).getAbsoluteFile().toString());
  }


  @Override
  public Git checkout(String userName, String projectName, String refName, String uri) {
    File cloneFile = getRepositoryFile(userName, projectName, refName);

    try {
      Git git;
      if (!cloneFile.exists()) {
        System.out.println("Cloning: " + uri);

        git = Git.cloneRepository()
            .setBranch(refName)
            .setURI(uri)
            .setDirectory(cloneFile)
            .call();
      } else {
        System.out.println("Checkout: " + uri);

        git = Git.open(cloneFile);
        git.checkout().setName(refName).setForce(true).call();
        git.pull().call();
      }

      return git;
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("user: " + userName);
      System.out.println("project: " + projectName);
      System.out.println("ref: " + refName);
      System.out.println("uri: " + uri);
      System.out.println("Could not clone using jgit. Attempt to clone via script.");

      try {
        ProcessBuilder ps = new ProcessBuilder(
            "sudo" , "/home/kosher/clone-repository.sh", cloneFile.getAbsolutePath(), uri, refName);

        //From the DOC:  Initially, this property is false, meaning that the
        //standard output and error output of a subprocess are sent to two
        //separate streams
        ps.redirectErrorStream(true);

        Process pr = ps.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
          System.out.println(line);
        }
        pr.waitFor();
        System.out.println("ok!");

        in.close();

        return Git.open(cloneFile);
      } catch (Exception e1) {
        throw new RuntimeException(e1);
      }
    }
  }
}

