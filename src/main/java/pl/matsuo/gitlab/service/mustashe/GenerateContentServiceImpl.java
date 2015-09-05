package pl.matsuo.gitlab.service.mustashe;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.MustacheResolver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.exception.ResourceNotFoundException;
import pl.matsuo.gitlab.service.build.jekyll.JekyllProperties;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE;


/**
 * Created by marek on 05.09.15.
 */
@Service
public class GenerateContentServiceImpl implements GenerateContentService {


  @Override
  public String generate(String user, String project, String branch, HttpServletRequest request,
                         File config, JekyllProperties properties) {
    try {
      File sourceRoot = new File(config.getParentFile(), properties.source());
      File styleRoot = new File(sourceRoot, properties.styleDirectory());

      String restOfTheUrl = ((String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
          .replaceFirst("/" + user + "/" + project + "/" + branch, "");

      // if markdown file exist, read it; if not 404
      File markdownSource;
      if (new File(sourceRoot, restOfTheUrl).exists()) {
        markdownSource = new File(sourceRoot, restOfTheUrl);
      } else if (new File(styleRoot, restOfTheUrl).exists()) {
        markdownSource = new File(styleRoot, restOfTheUrl);
      } else {
        throw new ResourceNotFoundException();
      }

      String template = FileUtils.readFileToString(markdownSource);

      // layout processing


      // mustache processing

      Map<String, Object> model = new HashMap<>();
      model.put("user_name", user);
      model.put("project_name", project);
      model.put("branch_name", branch);
      model.put("page_title", user + " - " + project + " - " + branch);
      model.put("base_href", getBaseHref(user, project, branch, request));

      MustacheResolver resolver = name -> {
        // Reader
        return null;
      };

      MustacheFactory mf = new DefaultMustacheFactory(resolver);
      Mustache mustache = mf.compile("template.mustache");
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      mustache.execute(new OutputStreamWriter(baos), model).flush();

      // markdown processing

      return new String(baos.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  protected String getBaseHref(String user, String project, String branch, HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    // todo: base href must contain absolute url - extract from request
    return url.substring(0, url.indexOf(String.join("/", user, project, branch))
        + String.join("/", user, project, branch).length()) + "/";
  }
}

