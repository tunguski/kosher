package pl.matsuo.gitlab.service.mustashe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.MustacheResolver;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.service.build.jekyll.JekyllProperties;
import pl.matsuo.gitlab.service.build.jekyll.model.SiteConfig;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.springframework.web.servlet.HandlerMapping.*;
import static pl.matsuo.gitlab.service.build.jekyll.JekyllProperties.*;


/**
 * Created by marek on 05.09.15.
 */
@Service
public class GenerateContentServiceImpl implements GenerateContentService {


  ObjectMapper mapper = new ObjectMapper(new YAMLFactory());


  @Override
  public String generate(String user, String project, String branch, HttpServletRequest request,
                         File config, JekyllProperties properties) {
    try {
      File sourceRoot = new File(config.getParentFile(), properties.source());
      File styleRoot = new File(config.getParentFile(), properties.styleDirectory());

      String restOfTheUrl = ((String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
          .replaceFirst("/" + user + "/" + project + "/" + branch, "");
      String mdRestOfUrl = restOfTheUrl.replaceAll("\\.html", ".md");

      // if markdown file exist, read it; if not 404
      String template = "";
      if (new File(sourceRoot, mdRestOfUrl).exists()) {
        template = FileUtils.readFileToString(new File(sourceRoot, mdRestOfUrl));
      } else if (new File(styleRoot, mdRestOfUrl).exists()) {
        template = FileUtils.readFileToString(new File(styleRoot, mdRestOfUrl));
      }
      template = template.trim();

      MultiSourceValueProvider<JsonNode> provider = new MultiSourceValueProvider<>(
          mapper.readTree(new File(styleRoot, ".kosher.yml")).get("site"),
          mapper.readTree(config).get("site"));

      boolean process = true;
      while (process) {
        // read custom configuration
        if (template.startsWith("---")) {
          String[] split = template.split("---", 3);
          provider = provider.sub(mapper.readTree(split[1]));
          template = (split.length > 2 ? split[2] : "").trim();
        }

        // layout processing
        String layout = provider.get(conf -> conf.get("layout").asText());
        String layoutBody = readFile(config, properties, "_layouts/" + layout + ".html");
        template = layoutBody.replaceFirst("\\{\\{\\s*content\\s*\\}\\}", template).trim();

        process = template.startsWith("---");
      }

      // mustache processing
      String mustache = mustache(user, project, branch, request, config, properties, template);

      // markdown processing


      return mustache;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  protected List<String> lookup(JekyllProperties properties) {
    return asList(properties.source(), properties.styleDirectory());
  }


  protected String readFile(File config, JekyllProperties properties, String path) {
    try {
      for (String directory : lookup(properties)) {
        File file = new File(new File(config.getParentFile(), directory), path);
        if (file.exists()) {
          // security - cannot escape from project directory by using ../../..
          if (!file.getCanonicalPath().startsWith(config.getParentFile().getCanonicalPath())) {
            throw new IllegalArgumentException("Illegal path: " + file.getCanonicalPath());
          }
          return readFileToString(file);
        }
      }

      return null;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  protected String mustache(String user, String project, String branch, HttpServletRequest request,
                            File config, JekyllProperties properties, String template) throws IOException {
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
    Mustache mustache = mf.compile(new StringReader(template), "page_template.md");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    mustache.execute(new OutputStreamWriter(baos), model).flush();

    return new String(baos.toByteArray());
  }


  protected String getBaseHref(String user, String project, String branch, HttpServletRequest request) {
    String url = request.getRequestURL().toString();
    // todo: base href must contain absolute url - extract from request
    return url.substring(0, url.indexOf(String.join("/", user, project, branch))
        + String.join("/", user, project, branch).length()) + "/";
  }
}

