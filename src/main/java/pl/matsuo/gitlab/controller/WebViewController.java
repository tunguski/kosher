package pl.matsuo.gitlab.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * Created by marek on 04.07.15.
 */
@Controller
@RequestMapping("/site")
public class WebViewController {


  @Value("${repositoryBase}")
  File repositoryBase;


  @RequestMapping(value = "/{user}/{project}/{branch}/**", method = GET)
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String get(@PathVariable("user") String user,
             @PathVariable("project") String project,
             @PathVariable("branch") String branch,
             HttpServletRequest request) {
    try {
      File projectBase = new File(repositoryBase, user + "/" + project + "/" + branch);

      if (new File(projectBase, ".kosher").exists()) {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(projectBase, ".kosher")));

        String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        String destination = properties.getProperty("jekyll.destination", "_site");
        File file = new File(new File(projectBase, destination), restOfTheUrl);
        if (file.exists()) {
          return FileUtils.readFileToString(file);
        } else {
          return "file not found: " + file.getAbsolutePath();
        }

      } else {
        return "site not found: " + projectBase.getAbsolutePath();
      }
    } catch (Exception e) {
      throw  new RuntimeException(e);
    }
  }
}

