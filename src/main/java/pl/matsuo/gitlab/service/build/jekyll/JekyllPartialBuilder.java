package pl.matsuo.gitlab.service.build.jekyll;

import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.build.PartialBuilder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;


/**
 * Created by marek on 04.07.15.
 */
@Service
public class JekyllPartialBuilder extends PartialBuilder {


  public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, Properties properties) {
    CompletableFuture<PartialBuildInfo> future = new CompletableFuture();
    PartialBuildInfo partialBuildInfo = new PartialBuildInfo();
    File projectBase = gitRepositoryService.repository(pushEvent);

    String siteBaseDir = properties.getProperty("jekyll.site", "src/site");

    // fixme: execute build
    ProcessBuilder pb = new ProcessBuilder("jekyll", "build");
    pb.environment().put("ci-build", "true");
    pb.directory(new File(projectBase, siteBaseDir));
    try {
      Process process = pb.start();
      process.waitFor();

      BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
      BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

      String log = "";

      // read the output from the command
      String s = null;
      while ((s = stdInput.readLine()) != null) {
        log = log + "\n" + s;
      }

      log = log + "\n\n";

      // read any errors from the attempted command
      while ((s = stdError.readLine()) != null) {
        log = log + "\n" + s;
      }

      partialBuildInfo.setLog(log);

    } catch (Exception e) {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      e.printStackTrace(new PrintWriter(os));
      partialBuildInfo.setLog(new String(os.toByteArray()));
    }

    future.complete(partialBuildInfo);

    return future;
  }
}

