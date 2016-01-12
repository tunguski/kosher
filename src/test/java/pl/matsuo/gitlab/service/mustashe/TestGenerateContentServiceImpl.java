package pl.matsuo.gitlab.service.mustashe;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.Mockito;
import pl.matsuo.gitlab.service.build.jekyll.JekyllProperties;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by marek on 06.09.15.
 */
public class TestGenerateContentServiceImpl {


  GenerateContentServiceImpl generateContentService = new GenerateContentServiceImpl();


  @Test
  public void testFunctions() {
    Map<String, Object> scopes = new HashMap<>();
    scopes.put("map", new HashMap<String, String>() {{
      put("key", "value");
    }});
    final Function<String, String> upperFunction = aInput -> aInput.toUpperCase();
    scopes.put("upper", upperFunction);


    Writer writer = new StringWriter();
    MustacheFactory mf = new DefaultMustacheFactory();
    Mustache mustache = mf.compile(new StringReader(
        "map.key={{map.key}}{{#upper}}, in #upper map.key={{map.key}}{{/upper}} \n" +
        "in #map{{#map}}, in #upper {{#upper}} keyInUpper={{key}} {{/upper}} {{/map}} "), "example");

    mustache.execute(writer, scopes);
    try
    {
      writer.flush();
    }
    catch (IOException e)
    {
      //
    }
    assertEquals("map.key=value, IN #UPPER MAP.KEY=VALUE \nin #map, in #upper  KEYINUPPER=VALUE   ", writer.toString());
  }

  @Test
  public void testProcessTemplate() throws Exception {
    File config = mock(File.class);
    JekyllProperties properties = mock(JekyllProperties.class);
    MultiSourceValueProvider provider = mock(MultiSourceValueProvider.class);
    String result = generateContentService.processTemplate(
        IOUtils.toString(getClass().getResourceAsStream("/markdown_test.md")), config, properties, provider);
    assertEquals(IOUtils.toString(getClass().getResourceAsStream("/markdown_result.html")), result);
  }


  @Test
  public void testReplaceComplex() throws Exception {
    BiConsumer<Matcher, StringBuffer> modifier = (matcher, sb) -> matcher.appendReplacement(sb, matcher.group() + matcher.group());
    BiConsumer<Matcher, StringBuffer> tail = (matcher, sb) -> matcher.appendTail(sb);
    assertEquals("ababz", generateContentService.replaceComplex("([abc]*)", "abz", modifier, tail));
  }
}

