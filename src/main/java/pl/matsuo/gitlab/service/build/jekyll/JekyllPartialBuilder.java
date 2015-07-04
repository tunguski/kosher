package pl.matsuo.gitlab.service.build.jekyll;

import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.build.PartialBuilder;

import java.util.concurrent.CompletableFuture;


/**
 * Created by marek on 04.07.15.
 */
@Service
public class JekyllPartialBuilder extends PartialBuilder {


  public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent) {
    CompletableFuture<PartialBuildInfo> future = new CompletableFuture();

    PartialBuildInfo partialBuildInfo = new PartialBuildInfo();

    // fixme: execute build

    future.complete(partialBuildInfo);

    return future;
  }
}

