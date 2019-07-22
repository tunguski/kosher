package pl.matsuo.gitlab.service.db;

import java.util.Collection;
import java.util.NavigableSet;
import java.util.function.Function;

/** Created by marek on 04.07.15. */
public interface Database {

  <V> String put(String key, V value);

  <V> V get(String key, Class<V> clazz);

  <V> V update(String key, Class<V> clazz, Function<V, V> exec);

  void delete(String key);

  NavigableSet<String> keySet();

  Collection<String> values();

  int size();

  boolean isEmpty();

  boolean containsKey(String key);
}
