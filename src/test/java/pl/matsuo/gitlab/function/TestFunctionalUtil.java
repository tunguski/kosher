package pl.matsuo.gitlab.function;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;
import static pl.matsuo.gitlab.function.FunctionalUtil.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.Test;

public class TestFunctionalUtil {

  @Test
  public void testAccess() throws Exception {
    AccessProvider<Integer> invocations = access(new Z(), Z.class.getField("invocations"));
    assertEquals((Integer) 0, invocations.get());
    invocations.accept(7);
    assertEquals((Integer) 7, invocations.get());
  }

  @Test
  public void testAccess1() throws Exception {
    Z z = new Z();
    AccessProvider<Z> access = access(z::getComplex, z::setComplex);
    assertNull(access.get());
    access.accept(z);
    assertEquals(z, access.get());
  }

  @Test
  public void testWith() throws Exception {
    AtomicBoolean invoked = new AtomicBoolean(false);
    with(
        "string",
        value -> {
          assertEquals("string", value);
          invoked.set(true);
        });
    assertTrue(invoked.get());
  }

  @Test
  public void testTransform() throws Exception {
    assertEquals((Integer) 7, transform("string", value -> value.equals("string") ? 7 : null));
  }

  @Test
  public void testWith1() throws Exception {
    AtomicBoolean invoked = new AtomicBoolean(false);
    with(
        "string",
        "string2",
        (v1, v2) -> {
          assertEquals("string", v1);
          assertEquals("string2", v2);
          invoked.set(true);
        });
    assertTrue(invoked.get());
  }

  @Test
  public void testIgnoreEx() throws Exception {
    ignoreEx(
        () -> {
          throw new RuntimeException("Should be ignored");
        });
  }

  @Test
  public void testIgnoreEx1() throws Exception {
    assertEquals("val", ignoreEx(() -> "val"));
    assertEquals(
        (Object) null,
        ignoreEx(
            () -> {
              throw new RuntimeException("Should be ignored");
            }));
  }

  @Test(expected = RuntimeException.class)
  public void testRuntimeEx() throws Exception {
    runtimeEx(
        () -> {
          throw new Exception("Should be wrapped into RuntimeException");
        });
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRuntimeEx1() throws Exception {
    runtimeEx(
        () -> {
          throw new Exception("Should be wrapped into RuntimeException");
        },
        ex -> {
          throw new IllegalArgumentException(ex);
        });
  }

  @Test
  public void testRuntimeEx2() throws Exception {
    assertEquals("val", runtimeEx(() -> "val"));
  }

  @Test(expected = RuntimeException.class)
  public void testRuntimeEx3() throws Exception {
    assertEquals(
        "val",
        runtimeEx(
            () -> {
              throw new Exception();
            }));
  }

  @Test
  public void testProcessEx() throws Exception {
    AtomicBoolean invoked = new AtomicBoolean(false);
    processEx(
        () -> {
          throw new Exception("Should be wrapped into RuntimeException");
        },
        ex -> {
          invoked.set(true);
        });
    assertTrue(invoked.get());
  }

  @Test
  public void testProcessEx1() throws Exception {
    AtomicBoolean invoked = new AtomicBoolean(false);
    assertEquals(
        "y",
        processEx(
            () -> {
              throw new Exception("Should be wrapped into RuntimeException");
            },
            ex -> {
              invoked.set(true);
              return "y";
            }));
    assertTrue(invoked.get());
  }

  @Test
  public void testProcessEx2() throws Exception {
    AtomicBoolean invoked = new AtomicBoolean(false);
    assertEquals(
        "x",
        processEx(
            () -> "x",
            ex -> {
              invoked.set(true);
              return "y";
            }));
    assertFalse(invoked.get());
  }

  @Test
  public void testOptional() throws Exception {
    assertEquals("x", optional("x").get());
  }

  @Test
  public void testCompose() throws Exception {
    AtomicBoolean invoked1 = new AtomicBoolean(false);
    AtomicBoolean invoked2 = new AtomicBoolean(false);

    compose(val -> invoked1.set(true), val -> invoked2.set(true)).accept("val");

    assertTrue(invoked1.get());
    assertTrue(invoked2.get());
  }

  @Test
  public void testEither() throws Exception {
    assertEquals((Integer) 1, either(true, () -> 1, () -> 2));
    assertEquals((Integer) 2, either(false, () -> 1, () -> 2));
  }

  @Test
  public void testRepeat() throws Exception {
    AtomicInteger counter = new AtomicInteger(3);
    AtomicInteger value = new AtomicInteger(0);

    repeat(
        () -> {
          counter.set(counter.get() - 1);
          return counter.get() >= 0;
        },
        () -> value.set(value.get() + 1));

    assertEquals(-1, counter.get());
    assertEquals(3, value.get());
  }

  @Test
  public void testCollectList() throws Exception {
    AtomicInteger counter = new AtomicInteger(3);

    List<Integer> integers =
        collectList(
            () -> {
              counter.set(counter.get() - 1);
              return counter.get() >= 0;
            },
            () -> counter.get());

    assertEquals(-1, counter.get());
    assertEquals(asList(2, 1, 0), integers);
  }

  @Test
  public void testStream() throws Exception {
    AtomicInteger counter = new AtomicInteger(3);

    Stream<Integer> stream =
        stream(
            () -> {
              counter.set(counter.get() - 1);
              return counter.get() >= 0;
            },
            () -> counter.get());

    assertEquals(asList(2, 1, 0), stream.collect(toList()));
    assertEquals(-1, counter.get());
  }

  class Z {

    public int invocations = 0;
    public Z complex;

    public Z getComplex() {
      return complex;
    }

    public void setComplex(Z complex) {
      this.complex = complex;
    }
  }
}
