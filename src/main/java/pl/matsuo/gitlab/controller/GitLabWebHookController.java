package pl.matsuo.gitlab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.matsuo.gitlab.hook.BuildInfo;
import pl.matsuo.gitlab.hook.PushEvent;
import pl.matsuo.gitlab.service.BuildService;

import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * Created by marek on 04.07.15.
 */
@Controller
@RequestMapping("/hooks")
public class GitLabWebHookController {


  @Autowired
  BuildService buildService;


  @RequestMapping(method = POST)
  public String pushEvent(@RequestBody PushEvent pushEvent) {
    return buildService.pushEvent(pushEvent);
  }


  @RequestMapping(value = "buildStatus/{idBuild}", method = GET)
  public BuildInfo buildStatus(@PathVariable("idBuild") String idBuild) {
    return buildService.buildStatus(idBuild);
  }
}

