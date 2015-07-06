package pl.matsuo.gitlab.service.build.cobertura;

import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.build.CommandExecutingPartialBuilder;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;


/**
 * Created by marek on 05.07.15.
 */
//@Service
public class CoberturaPartialBuilder extends CommandExecutingPartialBuilder {


  @Override
  public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, Properties properties) {
    return internalExecute(pushEvent, ".", "target",
        destination -> new String[] { "mvn", "cobertura:cobertura", "-DskipTests" },
        (partialBuildInfo, generationBase) -> {});
  }


  @Override
  public String getName() {
    return "cobertura";
  }
}

