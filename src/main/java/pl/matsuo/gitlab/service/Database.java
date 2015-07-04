package pl.matsuo.gitlab.service;

import java.util.Collection;
import java.util.NavigableSet;

/**
 * Created by marek on 04.07.15.
 */
public interface Database {

  <V> String put(String key, V value);

  <V> V get(String key, Class<V> clazz);

  NavigableSet<String> keySet();

  Collection<String> values();

  int size();

  boolean isEmpty();
}
