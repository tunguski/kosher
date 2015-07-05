package pl.matsuo.gitlab.service.build.docs;

import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.build.PartialBuilder;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;


/**
 * Created by marek on 05.07.15.
 */
@Service
public class DocumentationStylePartialBuilder extends PartialBuilder {


  @Override
  public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, Properties properties) {
    PartialBuildInfo partialBuildInfo = new PartialBuildInfo();
    partialBuildInfo.setName(getName());

    CompletableFuture<PartialBuildInfo> future = new CompletableFuture();
    future.complete(partialBuildInfo);

    return future;
  }


  @Override
  public String getName() {
    return "documentation";
  }
}

