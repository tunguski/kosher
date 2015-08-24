package pl.matsuo.gitlab.service.build.jekyll;

import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.build.CommandExecutingPartialBuilder;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;


/**
 * Created by marek on 04.07.15.
 */
@Service
public class JekyllPartialBuilder extends CommandExecutingPartialBuilder {


  public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, Properties properties) {
    return internalExecute(pushEvent, JekyllProperties.source(properties), JekyllProperties.destination(properties),
        destination -> new String[] { "jekyll", "build", "--destination", destination },
        (partialBuildInfo, generationBase) -> {});
  }


  @Override
  public String getName() {
    return "jekyll";
  }


  @Override
  public boolean shouldExecute(PushEvent pushEvent, Properties properties) {
    return true;
  }
}

