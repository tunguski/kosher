package pl.matsuo.gitlab.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * Created by marek on 04.07.15.
 */
@Controller
@RequestMapping("/_")
public class WebViewController {


  @Value("${repositoryBase}")
  String repositoryBase;


  @RequestMapping(method = GET)
  public String get(HttpRequest request) {
    String uri = request.getURI().toString();
    return uri;
  }
}

