package pl.matsuo.gitlab.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * Created by marek on 04.07.15.
 */
@Controller
@RequestMapping("/gl")
public class GitlabProxyController {


  @Value("#{environment.PRIVATE_TOKEN}")
  private String gitlabPrivateToken;
  @Value("#{environment.GITLAB_SERVER}")
  private String gitlabServer;


  @RequestMapping(value = "/projects/{user}/{project}/issues", method = GET)
  @ResponseStatus(OK)
  public @ResponseBody
  String projectIssues(@PathVariable("user") String user,
                       @PathVariable("project") String project,
                       @RequestParam(value = "labels", required = false) String labels,
                       @RequestParam(value = "milestone", required = false) String milestone) {
    return sendGet(gitlabServer + "/api/v3/projects/" + user + "%2F" + project + "/issues?"
        + (labels != null ? "labels=" + labels + "&" : "")
        + (milestone != null ? "milestone=" + milestone + "&" : ""));
  }


  // HTTP GET request
  private String sendGet(String url) {
    try {
      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();

      // optional default is GET
      con.setRequestMethod("GET");

      // set user agent
      con.setRequestProperty("User-Agent", "kosher/1.0");
      con.setRequestProperty("PRIVATE-TOKEN", gitlabPrivateToken);

      // FIXME: response codes from controller based on this response code
      int responseCode = con.getResponseCode();

      BufferedReader in = new BufferedReader(
          new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      return response.toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

