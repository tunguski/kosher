package pl.matsuo.gitlab.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
import java.io.IOException;

import static org.apache.commons.io.FileUtils.*;
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
        JekyllProperties properties = new JekyllProperties(config);

        String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        String destination = properties.destination();
        File file = new File(new File(config.getParentFile(), destination),
            restOfTheUrl.replaceFirst("/" + user + "/" + project + "/" + branch, ""));
        if (file.exists()) {
          // security - cannot escape from project directory by using ../../..
          if (!file.getCanonicalPath().startsWith(config.getParentFile().getCanonicalPath())) {
            System.out.println("file not found: " + file.getAbsolutePath());
            return getPageTemplate(user, project, branch, request);
          }

          String fileSource = readFileToString(file);
          return file.getName().endsWith(".html")
              ? fileSource.replace("<head>", "<head>\n<base href=\"" + getBaseHref(user, project, branch, request) + "\" />")
              : fileSource;
        } else {
          System.out.println("file not found: " + file.getAbsolutePath());
          return getPageTemplate(user, project, branch, request);
        }
      } catch (Exception e) {
        throw new ResourceNotFoundException();
      }
    }).orElseThrow(() -> new RuntimeException("No .kosher file found"));
  }


  protected String getPageTemplate(String user, String project, String branch,
                                   HttpServletRequest request) throws IOException {
    String url = request.getRequestURL().toString();
    if (url.endsWith(".js") || url.endsWith(".css")) {
      throw new ResourceNotFoundException();
    }

    String template = IOUtils.toString(getClass().getResourceAsStream("/templates/page.html"));

    // todo: base href must contain absolute url - extract from request
    String basePath = getBaseHref(user, project, branch, request);

    return template
        .replaceAll("#user_name#", user)
        .replaceAll("#project_name#", project)
        .replaceAll("#page_title#", user + " - " + project + " - " + branch)
        .replaceAll("#base_href#", basePath)
        ;
  }


  protected String getBaseHref(String user, String project, String branch, HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    // todo: base href must contain absolute url - extract from request
    return url.substring(0, url.indexOf(String.join("/", user, project, branch))
        + String.join("/", user, project, branch).length()) + "/";
  }
}

