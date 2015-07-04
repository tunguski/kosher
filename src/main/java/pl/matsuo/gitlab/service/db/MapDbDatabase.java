package pl.matsuo.gitlab.service.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapdb.DBMaker;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * Created by marek on 04.07.15.
 */
@Service
public class MapDbDatabase implements Database {


  MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
  ObjectMapper objectMapper = converter.getObjectMapper();
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
      return objectMapper.readValue(serialized, clazz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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
}

