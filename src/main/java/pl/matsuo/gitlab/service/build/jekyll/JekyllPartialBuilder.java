package pl.matsuo.gitlab.service.build.jekyll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.build.CommandExecutingPartialBuilder;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;


/**
 * Created by marek on 04.07.15.
 */
@Service @Order(Ordered.HIGHEST_PRECEDENCE)
public class JekyllPartialBuilder extends CommandExecutingPartialBuilder {


  @Autowired(required = false)
  List<JekyllGenerateTemplateService> jekyllGenerateTemplateServices;


  public CompletableFuture<PartialBuildInfo> internalExecute(PushEvent pushEvent, Properties properties) {
    JekyllProperties jekyllProperties = new JekyllProperties(properties);

//    if (jekyllGenerateTemplateServices != null) {
//      jekyllGenerateTemplateServices.forEach(generator -> generator.generateTemplate(pushEvent, jekyllProperties));
//    }

    return internalExecute(pushEvent, jekyllProperties.source(), jekyllProperties.destination(),
        destination -> new String[] { "jekyll", "build", "--destination", destination },
        (partialBuildInfo, generationBase) -> {});
  }


  @Override
  public boolean shouldExecute(PushEvent pushEvent, Properties properties) {
    return true;
  }
}

