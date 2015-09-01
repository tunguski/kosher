package pl.matsuo.gitlab.service.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapdb.DBMaker;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.function.Function;

/**
 * Created by marek on 04.07.15.
 */
@Service
public class MapDbDatabase implements Database {


  ObjectMapper objectMapper = new ObjectMapper();
  ConcurrentNavigableMap<String, String> db = DBMaker.newTempTreeMap();


  public <V> String put(String key, V value) {
    try {
      String serialized = objectMapper.writeValueAsString(value);
      return db.put(key, serialized);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }


  public <V> V get(String key, Class<V> clazz) {
    try {
      String serialized = db.get(key);
      if (serialized == null) {
        return null;
      } else if (clazz.equals(String.class)) {
        return (V) serialized;
      }

      return objectMapper.readValue(serialized, clazz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public <V> V update(String key, Class<V> clazz, Function<V, V> exec) {
    V result = exec.apply(get(key, clazz));

    if (result != null) {
      put(key, result);
    }

    return result;
  }


  public void delete(String key) {
    db.remove(key);
  }


  public NavigableSet<String> keySet() {
    return db.keySet();
  }


  public Collection<String> values() {
    return db.values();
  }


  public int size() {
    return db.size();
  }


  public boolean isEmpty() {
    return db.isEmpty();
  }

  @Override
  public boolean containsKey(String key) {
    return db.containsKey(key);
  }
}

