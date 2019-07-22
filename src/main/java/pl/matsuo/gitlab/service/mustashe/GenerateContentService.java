package pl.matsuo.gitlab.service.mustashe;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import pl.matsuo.gitlab.service.build.jekyll.JekyllProperties;

/** Created by marek on 05.09.15. */
public interface GenerateContentService {

  String generate(
      String user,
      String project,
      String branch,
      HttpServletRequest request,
      File config,
      JekyllProperties properties);
}
