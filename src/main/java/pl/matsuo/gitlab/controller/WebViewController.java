package pl.matsuo.gitlab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.matsuo.gitlab.exception.ResourceNotFoundException;
import pl.matsuo.gitlab.service.build.jekyll.JekyllProperties;
import pl.matsuo.gitlab.service.git.GitRepositoryService;
import pl.matsuo.gitlab.service.mustashe.GenerateContentService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.servlet.HandlerMapping.*;


/**
 * Created by marek on 04.07.15.
 */
@Controller
@RequestMapping("/{user}/{project}/{branch}")
public class WebViewController {


  @Autowired
  GitRepositoryService gitRepositoryService;
  @Autowired
  GenerateContentService generateContentService;


  @RequestMapping(value = "/**/*.html", method = GET, produces = "text/html;charset=UTF-8")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String getHtml(@PathVariable("user") String user,
             @PathVariable("project") String project,
             @PathVariable("branch") String branch,
             HttpServletRequest request) {
    return gitRepositoryService.getKosher(user, project, branch).map(config -> {
      try {
        JekyllProperties properties = new JekyllProperties(config, !new File(config.getParent(), "pom.xml").exists());
        String restOfTheUrl = ((String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
            .replaceFirst("/" + user + "/" + project + "/" + branch, "");

        String destination = properties.destination();
        File file = new File(new File(config.getParentFile(), destination), restOfTheUrl);
        // if url denotes directory, assumes it looks for index.html
        if (file.isDirectory()) {
          file = new File(file, "index.html");
        }

        // security - cannot escape from project directory by using ../../..
        if (!file.getCanonicalPath().startsWith(config.getParentFile().getCanonicalPath())) {
          throw new IllegalArgumentException("Illegal path: " + file.getCanonicalPath());
        }

        if (file.exists()) {
          // file exists in project directory
          return readFileToString(file);
        } else {
          // maybe file exists in style directory
          destination = properties.styleDirectory();
          file = new File(new File(config.getParentFile(), destination), restOfTheUrl);
          // if url denotes directory, assumes it looks for index.html
          if (file.isDirectory()) {
            file = new File(file, "index.html");
          }

          if (file.exists()) {
            // return file from style directory
            return readFileToString(file);
          }
        }
        return getPageTemplate(user, project, branch, request, config, properties);
      } catch (Exception e) {
        throw new ResourceNotFoundException();
      }
    }).orElseThrow(() -> new RuntimeException("No .kosher.yml file found"));
  }


  @RequestMapping(value = "/**/*", method = GET)
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String getResource(@PathVariable("user") String user,
             @PathVariable("project") String project,
             @PathVariable("branch") String branch,
             HttpServletRequest request) {

    return gitRepositoryService.getKosher(user, project, branch).map(config -> {
      try {
        JekyllProperties properties = new JekyllProperties(config, !new File(config.getParent(), "pom.xml").exists());

        String restOfTheUrl = ((String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
            .replaceFirst("/" + user + "/" + project + "/" + branch, "");

        String destination = properties.destination();
        File file = new File(new File(config.getParentFile(), destination), restOfTheUrl);
        if (file.exists()) {
          // security - cannot escape from project directory by using ../../..
          if (!file.getCanonicalPath().startsWith(config.getParentFile().getCanonicalPath())) {
            System.out.println("file not found: " + file.getAbsolutePath());
            throw new ResourceNotFoundException();
          }

          return readFileToString(file);
        } else {
          // if resource does not exist in project, maybe it should be find in style directory
          destination = properties.styleDirectory();
          file = new File(new File(config.getParentFile(), destination), restOfTheUrl);
          if (file.exists()) {
            // security - cannot escape from project directory by using ../../..
            if (!file.getCanonicalPath().startsWith(config.getParentFile().getCanonicalPath())) {
              System.out.println("file not found: " + file.getAbsolutePath());
              throw new ResourceNotFoundException();
            }

            return readFileToString(file);
          } else {
            System.out.println("file not found: " + file.getAbsolutePath());
            throw new ResourceNotFoundException();
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }).orElseThrow(() -> new RuntimeException("No .kosher file found"));
  }


  protected String getPageTemplate(String user, String project, String branch, HttpServletRequest request,
                                   File config, JekyllProperties properties) throws IOException {
    return generateContentService.generate(user, project, branch, request, config, properties);
  }
}

