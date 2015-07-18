package pl.matsuo.gitlab.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.HandlerMapping;
import pl.matsuo.gitlab.exception.ResourceNotFoundException;
import pl.matsuo.gitlab.service.build.jekyll.JekyllProperties;
import pl.matsuo.gitlab.service.git.GitRepositoryService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * Created by marek on 04.07.15.
 */
@Controller
@RequestMapping("/{user}/{project}/{branch}")
public class WebViewController {


  @Autowired
  GitRepositoryService gitRepositoryService;


  @RequestMapping(value = "/**", method = GET)
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String get(@PathVariable("user") String user,
             @PathVariable("project") String project,
             @PathVariable("branch") String branch,
             HttpServletRequest request) {

    return gitRepositoryService.getKosher(user, project, branch).map(config -> {
      try {
        Properties properties = new Properties();
        properties.load(new FileInputStream(config));

        String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        String destination = JekyllProperties.destination(properties);
        File file = new File(new File(config.getParentFile(), destination),
            restOfTheUrl.replaceFirst("/" + user + "/" + project + "/" + branch, ""));
        if (file.exists()) {
          // security - cannot escape from project directory by using ../../..
          if (!file.getCanonicalPath().startsWith(config.getParentFile().getCanonicalPath())) {
            return "file not found: " + file.getAbsolutePath();
          }

          return FileUtils.readFileToString(file);
        } else {
          return "file not found: " + file.getAbsolutePath();
        }
      } catch (Exception e) {
        throw new ResourceNotFoundException();
      }
    }).orElseThrow(() -> new RuntimeException("x"));
  }
}

