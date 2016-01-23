package pl.matsuo.gitlab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
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
import java.util.List;

import static java.util.Arrays.*;
import static org.apache.commons.io.FileUtils.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.servlet.HandlerMapping.*;
import static pl.matsuo.gitlab.function.FunctionalUtil.*;


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
        JekyllProperties properties = new JekyllProperties(config);
        String restOfTheUrl = ((String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
            .replaceFirst("/" + user + "/" + project + "/" + branch, "");

      String body = runtimeEx(() -> {
        File file = readFile(config, new JekyllProperties(config), restOfTheUrl);
        return file != null ? readFileToString(file) : null;
      });
      if (body != null) {
          return body;
        } else {
          String generated = generateContentService.generate(user, project, branch, request, config, properties);
          ignoreEx(() -> writeStringToFile(
              new File(new File(config.getParentFile(), properties.destination()), restOfTheUrl), generated));
          return generated;
        }
    }).orElseThrow(() -> new ResourceNotFoundException());
  }


  @RequestMapping(value = "/**/*.json", method = GET, produces = "application/json;charset=UTF-8")
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  String getJson(@PathVariable("user") String user,
             @PathVariable("project") String project,
             @PathVariable("branch") String branch,
             HttpServletRequest request) {
    return gitRepositoryService.getKosher(user, project, branch).map(config -> {
        JekyllProperties properties = new JekyllProperties(config);
        String restOfTheUrl = ((String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
            .replaceFirst("/" + user + "/" + project + "/" + branch, "");

      return processEx(() -> {
        File file = readFile(config, new JekyllProperties(config), restOfTheUrl);
        if (file != null) {
          return readFileToString(file);
        } else {
          throw new ResourceNotFoundException();
        }
      }, e -> { throw new ResourceNotFoundException(); });
    }).orElseThrow(() -> new ResourceNotFoundException());
  }


  @RequestMapping(value = "/**/*", method = GET)
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody
  FileSystemResource getResource(@PathVariable("user") String user,
             @PathVariable("project") String project,
             @PathVariable("branch") String branch,
             HttpServletRequest request) {
    return gitRepositoryService.getKosher(user, project, branch).map(config -> {
      String restOfTheUrl = ((String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
          .replaceFirst("/" + user + "/" + project + "/" + branch, "");

      File file = readFile(config, new JekyllProperties(config), restOfTheUrl);
      if (file != null) {
        return new FileSystemResource(file);
      } else {
        System.out.println("file not found: " + restOfTheUrl);
        throw new ResourceNotFoundException();
      }
    }).orElseThrow(() -> new ResourceNotFoundException());
  }


  protected List<String> lookup(JekyllProperties properties) {
    return asList(properties.destination(), properties.source(), properties.styleDirectory());
  }


  protected File readFile(File config, JekyllProperties properties, String path) {
    return runtimeEx(() -> {
      for (String directory : lookup(properties)) {
        File file = new File(new File(config.getParentFile(), directory), path);
        if (file.exists()) {
          // security:
          //    1) cannot escape from project directory by using ../../..
          //    2) hidden files are excluded
          if (!file.getCanonicalPath().startsWith(config.getParentFile().getCanonicalPath())
              || file.getName().startsWith(".")) {
            throw new IllegalArgumentException("Illegal path: " + file.getCanonicalPath());
          }
          return file;
        }
      }

      return null;
    });
  }
}

