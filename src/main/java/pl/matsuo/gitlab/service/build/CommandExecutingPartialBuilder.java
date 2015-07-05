package pl.matsuo.gitlab.service.build;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by marek on 05.07.15.
 */
public abstract class CommandExecutingPartialBuilder extends PartialBuilder {


  public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, String source, String destination,
                                                             Function<String, String[]> executionCommands,
                                                             Consumer<PartialBuildInfo> afterExecution) {
    PartialBuildInfo partialBuildInfo = new PartialBuildInfo();
    partialBuildInfo.setName(getName());

    CompletableFuture<PartialBuildInfo> future = new CompletableFuture();
    File projectBase = gitRepositoryService.repository(pushEvent);

    File generationBase = new File(projectBase, destination);
    if (!generationBase.mkdirs()) {
      try {
        // FileUtils is better at creating directories than JVM ;)
        FileUtils.forceMkdir(generationBase);
      } catch (IOException e) {
        e.printStackTrace();
        throw new RuntimeException("Cannot create site base directory: " + generationBase.getAbsolutePath());
      }
    }

    // fixme: execute build
    String[] command = executionCommands.apply(generationBase.getAbsolutePath());
    System.out.println("Executing command: " + Arrays.toString(command) + " in directory: " + new File(projectBase, source).getAbsolutePath());
    ProcessBuilder pb = new ProcessBuilder(command);
    pb.environment().put("ci-build", "true");
    pb.directory(new File(projectBase, source));
    try {
      Process process = pb.start();

      BufferedReader out = new BufferedReader(new InputStreamReader(process.getInputStream()));
      BufferedReader err = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String log = "";

      while (process.isAlive()) {
        System.out.println("...");
        process.waitFor(3, TimeUnit.SECONDS);

        while (out.ready()) {
          String line = out.readLine();
          System.out.println("    :: " + line);
          log = log + "\n" + line;
        }

        while (err.ready()) {
          String line = err.readLine();
          System.out.println("    !! " + line);
          log = log + "\n" + line;
        }
      }

      partialBuildInfo.setLog(log);
      partialBuildInfo.setExecutionResult(process.exitValue());
      if (process.exitValue() == 0) {
        partialBuildInfo.setStatus("ok");
      }

      afterExecution.accept(partialBuildInfo);
    } catch (Exception e) {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      e.printStackTrace(new PrintWriter(os));
      partialBuildInfo.setLog(new String(os.toByteArray()));
      e.printStackTrace();
    }

    future.complete(partialBuildInfo);

    return future;
  }
}

