package pl.matsuo.gitlab.service.build;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import pl.matsuo.gitlab.function.FunctionalUtil;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.db.Database;
import pl.matsuo.gitlab.util.TriFunction;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.apache.commons.io.FileUtils.*;
import static pl.matsuo.gitlab.function.FunctionalUtil.*;
import static pl.matsuo.gitlab.util.PushEventUtil.*;


/**
 * Created by marek on 05.07.15.
 */
public abstract class CommandExecutingPartialBuilder extends PartialBuilder {


  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  Database db;


  public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, String source, String destination,
                                                             Function<String, String[]> executionCommands,
                                                             BiConsumer<PartialBuildInfo, File> afterExecution) {
    PartialBuildInfo partialBuildInfo = new PartialBuildInfo();
    partialBuildInfo.setName(getName());

    CompletableFuture<PartialBuildInfo> future = new CompletableFuture();
    File projectBase = gitRepositoryService.repository(pushEvent);

    File generationBase = new File(projectBase, destination);
    if (!generationBase.mkdirs()) {
      // FileUtils is better at creating directories than JRE
      runtimeEx(() -> forceMkdir(generationBase), e -> {
        e.printStackTrace();
        throw new RuntimeException("Cannot create site base directory: " + generationBase.getAbsolutePath());
      });
    }

    // fixme: execute build
    String[] command = executionCommands.apply(generationBase.getAbsolutePath());
    System.out.println("Executing command: " + Arrays.toString(command) + " in directory: "
        + new File(projectBase, source).getAbsolutePath());
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

      db.put(commit(pushEvent, getName(), "log"), log);
      partialBuildInfo.setExecutionResult(process.exitValue());

      afterExecution.accept(partialBuildInfo, generationBase);

      if (process.exitValue() == 0) {
        partialBuildInfo.setStatus("ok");
      } else {
        partialBuildInfo.setStatus("error");
      }
    } catch (Exception e) {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      e.printStackTrace(new PrintWriter(os));
      db.put(commit(pushEvent, getName(), "log"), new String(os.toByteArray()));

      partialBuildInfo.setStatus("error");

      e.printStackTrace();
    }

    future.complete(partialBuildInfo);

    return future;
  }


  public BiConsumer<PartialBuildInfo, File> executeWithReport(PushEvent pushEvent,
                                                              String reportName,
                                                              TriFunction<PartialBuildInfo, File, String, ?> exec) {
    return (partialBuildInfo, generationBase) -> {
      File reportFile = new File(generationBase, reportName);
      if (reportFile.exists()) {
        runtimeEx(() -> {
          String reportBody = readFileToString(reportFile);
          String idReport = commit(pushEvent, getName(), "file");
          Object report = exec.apply(partialBuildInfo, generationBase, reportBody);
          database.put(idReport, objectMapper.writeValueAsString(report));
        });
      } else {
        throw new RuntimeException("Could not find " + reportName + " file");
      }
    };
  }
}

