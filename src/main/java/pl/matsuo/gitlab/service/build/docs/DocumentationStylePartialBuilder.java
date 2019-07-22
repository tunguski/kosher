package pl.matsuo.gitlab.service.build.docs;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.build.PartialBuilder;

/** Created by marek on 05.07.15. */
@Service
public class DocumentationStylePartialBuilder extends PartialBuilder {

  @Override
  public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, File properties) {
    PartialBuildInfo partialBuildInfo = new PartialBuildInfo();
    partialBuildInfo.setName(getName());

    CompletableFuture<PartialBuildInfo> future = new CompletableFuture();
    future.complete(partialBuildInfo);

    return future;
  }

  @Override
  public boolean shouldExecute(PushEvent pushEvent, File properties) {
    return true;
  }
}
