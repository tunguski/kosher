package pl.matsuo.gitlab.service.mustashe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.MustacheResolver;
import com.github.mustachejava.TemplateFunction;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.pegdown.PegDownProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.matsuo.gitlab.service.build.jekyll.JekyllProperties;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.*;
import static org.apache.commons.io.FileUtils.*;
import static org.pegdown.Extensions.*;
import static org.springframework.web.servlet.HandlerMapping.*;
import static pl.matsuo.gitlab.function.FunctionalUtil.*;


/**
 * Created by marek on 05.09.15.
 */
@Service
public class GenerateContentServiceImpl implements GenerateContentService {


  @Value("#{environment.GITLAB_SERVER}")
  private String gitlabServer;


  // HARDWRAPS,AUTOLINKS,FENCED_CODE_BLOCKS,DEFINITIONS,TABLES
  // may render page up to 30 seconds - it is done once and cached
  PegDownProcessor processor = new PegDownProcessor(TABLES | DEFINITIONS | FENCED_CODE_BLOCKS | AUTOLINKS, 30000);
  ObjectMapper mapper = new ObjectMapper(new YAMLFactory());



  @Override
  public String generate(String user, String project, String branch, HttpServletRequest request,
                         File config, JekyllProperties properties) {
    return runtimeEx(() -> {
      File sourceRoot = new File(config.getParentFile(), properties.source());
      File styleRoot = new File(config.getParentFile(), properties.styleDirectory());

      String restOfTheUrl = ((String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
          .replaceFirst("/" + user + "/" + project + "/" + branch, "");
      String mdRestOfUrl = restOfTheUrl.replaceAll("\\.html", ".md");
      // if request points to directory, return index file from this directory
      if (mdRestOfUrl.endsWith("/")) {
        mdRestOfUrl = mdRestOfUrl + "index.md";
      }

      // if markdown file exist, read it; if not 404
      String template = "";
      if (new File(sourceRoot, mdRestOfUrl).exists()) {
        template = FileUtils.readFileToString(new File(sourceRoot, mdRestOfUrl));
      } else if (new File(styleRoot, mdRestOfUrl).exists()) {
        template = FileUtils.readFileToString(new File(styleRoot, mdRestOfUrl));
      }

      MultiSourceValueProvider provider = new MultiSourceValueProvider(
          mapper.readTree(config),
          mapper.readTree(config),
          mapper.readTree(new File(styleRoot, ".kosher.yml")),
          buildModel(user, project, branch, request));

      template = processTemplate(template, config, properties, provider);

      // mustache processing
      String mustache = mustache(config, properties, template, provider);

      // replace {[{ }]} with {{ }} - for default angular brackets
      mustache = mustache.replaceAll("\\{\\[\\{", "{{").replaceAll("\\}\\]\\}", "}}");

      return mustache;
    });
  }


  protected String processTemplate(String template, File config, JekyllProperties properties,
                                   MultiSourceValueProvider provider) throws IOException {
    template = template.trim();

    boolean demarkdownified = false;
    boolean process = true;
    while (process) {
      // read custom configuration
      if (template.startsWith("---")) {
        String[] split = template.split("---", 3);
        provider = provider.add(mapper.readTree(split[1]));
        template = (split.length > 2 ? split[2] : "").trim();
      }

      // escape all spaces in links
      template = replaceComplex("(\\[[^]]+\\]\\(([^)]*\\([^)]*\\))*[^)]*\\))", template,
          (matcher, sb) -> {
            String[] split = matcher.group().split("\\]", 2);
            String linkName = String.join("]", split[0], split[1].replaceAll(" ", "%20"));
            matcher.appendReplacement(sb, linkName);
          }, null);

      // escape minus signs in tables
      template = replaceComplex("(\\|[ ]*-[ ]*\\|)", template,
          (matcher, sb) -> {
            String linkName = matcher.group().replaceAll("-", "\\\\\\\\\\\\-");
            matcher.appendReplacement(sb, linkName);
          }, null);

      if (!demarkdownified) {
        // markdown processing
        template = processor.markdownToHtml(template);
        demarkdownified = true;
      }

      // layout processing
      String layout = provider.get(conf -> conf.get("layout") != null ? conf.get("layout").asText() : null);
      if (layout != null) {
        String layoutBody = readFile(config, properties, "_layouts/" + layout + ".html");
        template = layoutBody.replaceAll("\\{\\{\\s*content\\s*\\}\\}", template.replaceAll("\\$", "\\\\\\$")).trim();
      }

      process = template.startsWith("---");
    }

    return template;
  }


  protected String replaceComplex(String regex, String template,
                                  BiConsumer<Matcher, StringBuffer> modifier, BiConsumer<Matcher, StringBuffer> tail) {
    Pattern linkRegex = Pattern.compile(regex);
    Matcher matcher = linkRegex.matcher(template);

    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      modifier.accept(matcher, sb);
    }
    if (tail != null) {
      tail.accept(matcher, sb);
    } else {
      matcher.appendTail(sb);
    }

    return sb.toString();
  }


  private JsonNode buildModel(String user, String project, String branch, HttpServletRequest request) throws IOException {
    String restOfTheUrl = ((String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE))
        .replaceFirst("/" + user + "/" + project + "/" + branch, "")
        .replaceAll("/+", "/");

    // FIXME: for java projects should be src/main/site; use config in general
    String base = "./";
    for (int i = 0; i < restOfTheUrl.split("/").length - 2; i++) {
      base = base + "../";
    }

    return mapper.readTree(
        "project:" +
      "\n  user: " + user +
      "\n  name: " + project +
      "\n  branch: " + branch +
      "\npage: " +
      "\n  title: " + user + " - " + project + " - " + branch +
      "\n  href: " + getBaseHref(user, project, branch, request) +
      "\nbase: " + base +
      "\nbase_gitlab: " + gitlabServer);
  }


  protected List<String> lookup(JekyllProperties properties) {
    return asList(properties.source(), properties.styleDirectory());
  }


  protected String readFile(File config, JekyllProperties properties, String path) {
    return runtimeEx(() -> {
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
    });
  }


  protected String mustache(File config, JekyllProperties properties, String template,
                            MultiSourceValueProvider provider) throws IOException {
    MustacheResolver resolver = name -> {
      String content = readFile(config, properties, "_includes/" + name);
      return content != null ? new StringReader(content) : null;
    };

    MustacheFactory mf = new DefaultMustacheFactory(resolver);
    Mustache mustache = mf.compile(new StringReader(template), "page_template.md");

    Map<String, Object> model = provider.asMap();
    model.put("renderDynamic", (TemplateFunction) templateNameField -> {
      String templateName = (String) model.get(templateNameField);
      if (templateName != null) {
        Reader reader = resolver.getReader(templateName);
        if (reader != null) {
          runtimeEx(() -> String.join("\n", IOUtils.readLines(reader)));
        }
      }
      return "";
    });

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

