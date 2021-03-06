package pl.matsuo.gitlab.controller;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.matsuo.gitlab.data.BuildInfo;
import pl.matsuo.gitlab.exception.ResourceNotFoundException;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.build.BuildService;

/** Created by marek on 04.07.15. */
@Controller
@RequestMapping("/hooks")
public class GitLabWebHookController {

  @Autowired BuildService buildService;

  @RequestMapping(method = POST)
  public @ResponseBody String pushEvent(@RequestBody PushEvent pushEvent) {
    return buildService.pushEvent(pushEvent);
  }

  @RequestMapping(value = "buildStatus/{idBuild}", method = GET)
  public @ResponseBody BuildInfo buildStatus(@PathVariable("idBuild") String idBuild) {
    BuildInfo buildInfo = buildService.buildStatus(idBuild);

    if (buildInfo == null) {
      throw new ResourceNotFoundException();
    }

    return buildInfo;
  }
}
