package pl.matsuo.gitlab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.matsuo.gitlab.service.db.Database;

import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * Created by marek on 04.07.15.
 */
@Controller
@RequestMapping("/{user}/{project}/{branch}")
public class CodeStatisticsController {


  @Autowired
  Database database;


  @RequestMapping(value = "checkstyle", method = GET)
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String checkstyle(@PathVariable("user") String user,
                    @PathVariable("project") String project,
                    @PathVariable("branch") String branch) {
    return "checkstyle";
  }


  @RequestMapping(value = "findbugs", method = GET)
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String findbugs(@PathVariable("user") String user,
             @PathVariable("project") String project,
             @PathVariable("branch") String branch) {
    return "findbugs";
  }


  @RequestMapping(value = "javancss", method = GET)
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String javancss(@PathVariable("user") String user,
             @PathVariable("project") String project,
             @PathVariable("branch") String branch) {
    return "javancss";
  }


  @RequestMapping(value = "pmd", method = GET)
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String pmd(@PathVariable("user") String user,
             @PathVariable("project") String project,
             @PathVariable("branch") String branch) {
    return "pmd";
  }
}

