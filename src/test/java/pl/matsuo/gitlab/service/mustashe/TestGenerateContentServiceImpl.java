package pl.matsuo.gitlab.service.mustashe;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Created by marek on 06.09.15.
 */
public class TestGenerateContentServiceImpl {


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
    Mustache mustache = mf.compile(new StringReader("map.key={{map.key}}{{#upper}}, in #upper map.key={{map.key}}{{/upper}} \n" +
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
}