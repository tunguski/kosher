package pl.matsuo.gitlab.function;

import static org.junit.Assert.*;
import static pl.matsuo.gitlab.function.Optional.*;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

public class TestOptional {

  @Test(expected = NoSuchElementException.class)
  public void testEmpty() throws Exception {
    assertFalse(empty().isPresent());
    empty().get();
  }

  @Test
  public void testOf() throws Exception {
    assertTrue(of(1).isPresent());
    assertEquals("x", of("x").get());
  }

  @Test
  public void testOfNullable() throws Exception {
    assertTrue(ofNullable(7).isPresent());
    assertFalse(ofNullable(null).isPresent());
  }

  @Test
  public void testGet() throws Exception {
    assertEquals("7", of("7").get());
  }

  @Test
  public void testIsPresent() throws Exception {
    assertTrue(of(1).isPresent());
    assertFalse(ofNullable(null).isPresent());
  }

  @Test
  public void testIfPresent() throws Exception {
    AtomicBoolean touched = new AtomicBoolean(false);
    of(1).ifPresent(val -> touched.set(true));
    assertTrue(touched.get());
  }

  @Test
  public void testFilter() throws Exception {
    assertEquals("x", of("x").filter(val -> val.equals("x")).get());
    assertFalse(of("x").filter(val -> val.equals("y")).isPresent());
  }

  @Test
  public void testMap() throws Exception {
    assertEquals("y", of("x").map(val -> "y").get());
    assertFalse(ofNullable(null).map(val -> "y").isPresent());
  }

  @Test
  public void testFlatMap() throws Exception {
    assertEquals("y", of("x").flatMap(val -> of("y")).get());
    assertFalse(ofNullable(null).flatMap(val -> of("y")).isPresent());
  }

  @Test
  public void testOrElse() throws Exception {
    assertEquals("y", ofNullable(null).orElse("y"));
    assertEquals("x", of("x").orElse("y"));
  }

  @Test
  public void testOrElseGet() throws Exception {
    assertEquals("y", ofNullable(null).orElseGet(() -> "y"));
    assertEquals("x", of("x").orElseGet(() -> "y"));
  }

  @Test(expected = RuntimeException.class)
  public void testOrElseThrow() throws Exception {
    ofNullable(null).orElseThrow(() -> new RuntimeException());
  }

  @Test
  public void testOrElseThrow2() throws Exception {
    assertEquals("x", of("x").orElseThrow(() -> new RuntimeException()));
  }

  @Test
  public void testOr() throws Exception {
    assertEquals(of("y"), ofNullable(null).or("y"));
    assertEquals(of("x"), of("x").or("y"));
  }

  @Test
  public void testOrGet() throws Exception {
    assertEquals(of("y"), ofNullable(null).orGet(() -> "y"));
    assertEquals(of("x"), of("x").orGet(() -> "y"));
  }

  @Test(expected = RuntimeException.class)
  public void testOrThrow() throws Exception {
    ofNullable(null).orThrow(() -> new RuntimeException());
  }

  @Test
  public void testOrThrow2() throws Exception {
    assertEquals(of("x"), of("x").orThrow(() -> new RuntimeException()));
  }
}
