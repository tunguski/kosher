package pl.matsuo.gitlab.service.mustashe;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.Arrays.*;

/**
 * Created by marek on 05.09.15.
 */
public class MultiSourceValueProvider {

  private final List<JsonNode> sources;


  public MultiSourceValueProvider(JsonNode ... sources) {
    this.sources = asList(sources);
  }


  public MultiSourceValueProvider(List<JsonNode> sources) {
    this.sources = new ArrayList<>(sources);
  }


  public <F> F get(Function<JsonNode, F> getter) {
    for (JsonNode source : sources) {
      F apply = getter.apply(source);
      if (apply != null) {
        return apply;
      }
    }

    return null;
  }


  public MultiSourceValueProvider sub(JsonNode source) {
    List<JsonNode> sources = new ArrayList<>();
    sources.add(source);
    sources.addAll(this.sources);
    return new MultiSourceValueProvider(sources);
  }


  public Map<String, Object> asMap() {
    return new Map<String, Object>() {
      @Override
      public int size() {
        throw new RuntimeException();
      }

      @Override
      public boolean isEmpty() {
        throw new RuntimeException();
      }

      @Override
      public boolean containsKey(Object key) {
        String[] split = ((String) key).split("\\.");

        for (JsonNode source : sources) {
          JsonNode node = source;

          for (String part : split) {
            node = node.get(part);
            if (node == null) {
              break;
            }
          }

          if (node != null) {
            return true;
          }
        }

        return false;
      }

      @Override
      public boolean containsValue(Object value) {
        throw new RuntimeException();
      }

      @Override
      public Object get(Object key) {
        String[] split = ((String) key).split("\\.");

        for (JsonNode source : sources) {
          JsonNode node = source;

          for (String part : split) {
            node = node.get(part);
            if (node == null) {
              break;
            }
          }

          if (node != null) {
            return node.asText();
          }
        }

        return null;
      }

      @Override
      public Object put(String key, Object value) {
        throw new RuntimeException();
      }

      @Override
      public Object remove(Object key) {
        throw new RuntimeException();
      }

      @Override
      public void putAll(Map<? extends String, ?> m) {
        throw new RuntimeException();
      }

      @Override
      public void clear() {
        throw new RuntimeException();
      }

      @Override
      public Set<String> keySet() {
        throw new RuntimeException();
      }

      @Override
      public Collection<Object> values() {
        throw new RuntimeException();
      }

      @Override
      public Set<Entry<String, Object>> entrySet() {
        throw new RuntimeException();
      }
    };
  }
}

