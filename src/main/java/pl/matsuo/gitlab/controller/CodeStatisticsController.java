package pl.matsuo.gitlab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.matsuo.gitlab.data.ProjectInfo;
import pl.matsuo.gitlab.data.UserInfo;
import pl.matsuo.gitlab.service.db.Database;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static pl.matsuo.gitlab.util.PushEventUtil.*;


/**
 * Created by marek on 04.07.15.
 */
@Controller
@RequestMapping("/s")
public class CodeStatisticsController {


  @Autowired
  Database db;


  @RequestMapping(value = "/", method = GET)
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String all() {
    String result = "";

    for (String key : db.keySet()) {
      result += key + ": " + db.get(key, String.class) + "\n\n";
    }

    return result;
  }


  @RequestMapping(value = "/{user}", method = GET)
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  UserInfo user(@PathVariable("user") String user) {
    return db.get(user, UserInfo.class);
  }


  @RequestMapping(value = "/{user}/{project}", method = GET)
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  ProjectInfo project(@PathVariable("user") String user,
                 @PathVariable("project") String project) {
    return db.get(subPath(user, project), ProjectInfo.class);
  }


  @RequestMapping(value = "/{user}/{project}/{branch}", method = GET)
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String branch(@PathVariable("user") String user,
                   @PathVariable("project") String project,
                   @PathVariable("branch") String branch) {
    return db.get(subPath(user, project, branch), String.class);
  }


  @RequestMapping(value = "/{user}/{project}/{branch}/{commit}/{element}/{part}", method = GET)
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String partialInfo(@PathVariable("user") String user,
                   @PathVariable("project") String project,
                   @PathVariable("branch") String branch,
                   @PathVariable("commit") String commit,
                   @PathVariable("element") String element,
                     @PathVariable("part") String part) {
    return db.get(subPath(user, project, branch, commit, element, part), String.class);
  }
}

