package pl.matsuo.gitlab.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * Created by marek on 04.07.15.
 */
@Controller
@RequestMapping("/gl")
public class GitlabProxyController {


  @Value("${PRIVATE_TOKEN}")
  private String gitlabPrivateToken;


  @RequestMapping(value = "/projects/{idProject}/issues", method = GET)
  @ResponseStatus(OK)
  public @ResponseBody
  String projectIssues(@PathVariable("idProject") Integer idProject,
                       @RequestParam("label") String labels,
                       @RequestParam("milestone") String milestone) {
    return "" + idProject;
  }
}

