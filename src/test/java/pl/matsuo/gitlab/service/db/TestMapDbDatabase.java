package pl.matsuo.gitlab.service.db;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Created by marek on 04.07.15.
 */
public class TestMapDbDatabase {


  MapDbDatabase database = new MapDbDatabase();


  @Test
  public void testPutAndGet() throws Exception {
    database.put("test", "value");
    assertEquals("value", database.get("test", String.class));
  }


  @Test
  public void testKeySet() throws Exception {
    database.put("test", "value");
    assertEquals(1, database.keySet().size());
  }


  @Test
  public void testValues() throws Exception {
    database.put("test", "value");
    assertEquals(1, database.values().size());
  }


  @Test
  public void testSize() throws Exception {
    database.put("test", "value");
    assertEquals(1, database.size());
  }


  @Test
  public void testIsEmpty() throws Exception {
    assertTrue(database.isEmpty());
    database.put("test", "value");
    assertFalse(database.isEmpty());
  }
}

