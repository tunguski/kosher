package pl.matsuo.gitlab.service.mustashe;


import pl.matsuo.gitlab.service.build.jekyll.JekyllProperties;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * Created by marek on 05.09.15.
 */
public interface GenerateContentService {


  String generate(String user, String project, String branch, HttpServletRequest request,
                  File config, JekyllProperties properties);
}

