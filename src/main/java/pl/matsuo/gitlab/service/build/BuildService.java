package pl.matsuo.gitlab.service.build;

import org.springframework.web.bind.annotation.RequestBody;
import pl.matsuo.gitlab.data.BuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;

/** Created by marek on 04.07.15. */
public interface BuildService {

  String pushEvent(@RequestBody PushEvent pushEvent);

  BuildInfo buildStatus(String idBuild);
}
