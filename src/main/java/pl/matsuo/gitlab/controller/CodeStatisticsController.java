package pl.matsuo.gitlab.controller;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static pl.matsuo.gitlab.util.PushEventUtil.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.matsuo.gitlab.data.BuildInfo;
import pl.matsuo.gitlab.data.ProjectInfo;
import pl.matsuo.gitlab.data.UserInfo;
import pl.matsuo.gitlab.exception.ResourceNotFoundException;
import pl.matsuo.gitlab.hook.PartialBuildInfo;
import pl.matsuo.gitlab.service.db.Database;

/** Created by marek on 04.07.15. */
@Controller
@RequestMapping("/s")
public class CodeStatisticsController {

  @Autowired Database db;

  @RequestMapping(value = "/{user}", method = GET)
  @ResponseStatus(OK)
  public @ResponseBody UserInfo user(@PathVariable("user") String user) {
    return db.get(user, UserInfo.class);
  }

  @RequestMapping(value = "/{user}/{project}", method = GET)
  @ResponseStatus(OK)
  public @ResponseBody ProjectInfo project(
      @PathVariable("user") String user, @PathVariable("project") String project) {
    return db.get(subPath(user, project), ProjectInfo.class);
  }

  @RequestMapping(value = "/{user}/{project}/{branch}", method = GET)
  @ResponseStatus(OK)
  public @ResponseBody Object branch(
      @PathVariable("user") String user,
      @PathVariable("project") String project,
      @PathVariable("branch") String branch) {
    String data = db.get(subPath(user, project, branch), String.class);

    if (data != null && data.contains("{")) {
      // returns BuildInfo
      return db.get(subPath(user, project, branch), BuildInfo.class);
    } else {
      // returns commit reference
      return data;
    }
  }

  @RequestMapping(value = "/{user}/{project}/{commit}/{element}", method = GET)
  @ResponseStatus(OK)
  public @ResponseBody PartialBuildInfo element(
      @PathVariable("user") String user,
      @PathVariable("project") String project,
      @PathVariable("commit") String commit,
      @PathVariable("element") String element) {
    BuildInfo buildInfo = db.get(subPath(user, project, commit), BuildInfo.class);
    PartialBuildInfo data = buildInfo.getPartialStatuses().get(element);
    if (data == null) {
      throw new ResourceNotFoundException();
    }
    return data;
  }

  @RequestMapping(value = "/{user}/{project}/{commit}/{element}/{subelement}", method = GET)
  @ResponseStatus(OK)
  public @ResponseBody String subelement(
      @PathVariable("user") String user,
      @PathVariable("project") String project,
      @PathVariable("commit") String commit,
      @PathVariable("element") String element,
      @PathVariable("subelement") String subelement) {
    String data = db.get(subPath(user, project, commit, element, subelement), String.class);
    if (data == null) {
      throw new ResourceNotFoundException();
    }
    return data;
  }

  @RequestMapping(value = "/{user}/{project}/{commit}/{element}/{subelement}/**", method = GET)
  @ResponseStatus(NOT_FOUND)
  public void subelement() {}
}
