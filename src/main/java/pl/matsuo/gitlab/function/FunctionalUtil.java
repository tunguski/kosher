package pl.matsuo.gitlab.function;

import static java.util.Spliterator.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Functional programming utils. Created by marek on 27.03.14. */
public class FunctionalUtil {
  private static final Logger logger = LoggerFactory.getLogger(FunctionalUtil.class);

  /**
   * Objects provide access to some element. It may be getter/setter pair or get/set field directly.
   * Abstracted over implementation.
   *
   * @param <E> Accessed value type
   */
  public static class AccessProvider<E> implements Supplier<E>, Consumer<E> {

    private final Supplier<E> getter;
    private final Consumer<E> setter;

    AccessProvider(Supplier<E> getter, Consumer<E> setter) {
      this.getter = getter;
      this.setter = setter;
    }

    public AccessProvider setIfNull(
        Supplier<? extends E> creator, Consumer<? super E> initializer) {
      if (getter.get() == null) {
        E object = creator.get();
        initializer.accept(object);
        setter.accept(object);
      }
      return this;
    }

    public AccessProvider setIfNull(Supplier<? extends E> creator) {
      return setIfNull(creator, object -> {});
    }

    public AccessProvider invoke(Consumer<E> callable) {
      callable.accept(getter.get());
      return this;
    }

    @Override
    public void accept(E e) {
      setter.accept(e);
    }

    @Override
    public E get() {
      return getter.get();
    }
  }

  /** Create access provider from getter/setter pair. */
  public static <E> AccessProvider<E> access(Supplier<E> getter, Consumer<E> setter) {
    return new AccessProvider<E>(getter, setter);
  }

  /** Create access provider from field on object. */
  public static <E> AccessProvider<E> access(Object object, Field field) {
    return access(
        () ->
            ignoreEx(
                () -> {
                  return (E) field.get(object);
                }),
        value ->
            ignoreEx(
                () -> {
                  field.set(object, value);
                  return null;
                }));
  }

  /** Invoke consumer with object as parameter. */
  public static <E> E with(E object, Consumer<E> invoke) {
    invoke.accept(object);
    return object;
  }

  /** Return function invocation result with object as parameter. */
  public static <E, F> F transform(E object, Function<E, F> invoke) {
    return invoke.apply(object);
  }

  /** Invoke consumer with two objects as parameters. */
  public static <E, F> void with(E object, F object2, BiConsumer<E, F> invoke) {
    invoke.accept(object, object2);
  }

  /** Consumer of exception that logs error. */
  public static final Consumer<Exception> ignoringExConsumer =
      e -> logger.warn("Ignoring exception", e);
  /** Consumer that throws RuntimeException with passed exception as root cause. */
  public static final Consumer<Exception> runtimeExConsumer =
      e -> {
        throw new RuntimeException(e);
      };

  /** Invoke supplier and return null on exception. */
  public static <E> E ignoreEx(ThrowingExceptionsSupplier<E> throwsExceptions) {
    return processEx(
        throwsExceptions,
        e -> {
          logger.warn("Ignoring exception", e);
          return null;
        });
  }

  /** Invoke runnable and ignore exceptions. */
  public static void ignoreEx(ThrowingExceptionsRunnable throwsExceptions) {
    processEx(throwsExceptions, ignoringExConsumer);
  }

  /** Invoke supplier and throw RuntimeException on exception. */
  public static <E> E runtimeEx(ThrowingExceptionsSupplier<E> throwsExceptions) {
    return processEx(
        throwsExceptions,
        e -> {
          throw new RuntimeException(e);
        });
  }

  /** Invoke runnable and throw RuntimeException on exception. */
  public static void runtimeEx(ThrowingExceptionsRunnable throwsExceptions) {
    runtimeEx(throwsExceptions, runtimeExConsumer);
  }

  /** Invoke runnable with exception handler. */
  public static void runtimeEx(
      ThrowingExceptionsRunnable throwsExceptions, Consumer<Exception> processEx) {
    processEx(throwsExceptions, e -> processEx.accept(e));
  }

  /** Invoke supplier with exception handler. */
  public static <E> E processEx(
      ThrowingExceptionsSupplier<E> throwsExceptions, Function<Exception, E> processEx) {
    try {
      return throwsExceptions.get();
    } catch (Exception e) {
      return processEx.apply(e);
    }
  }

  /** Invoke runnable with exception handler. */
  public static void processEx(
      ThrowingExceptionsRunnable throwsExceptions, Consumer<Exception> processEx) {
    try {
      throwsExceptions.run();
    } catch (Exception e) {
      processEx.accept(e);
    }
  }

  /** Build Optional from value that may be null. */
  public static <E> Optional<E> optional(E value) {
    return Optional.ofNullable(value);
  }

  /** Create consumer that invokes list of inner consumers. */
  public static <E> Consumer<E> compose(Consumer<? super E>... parts) {
    return value -> {
      for (Consumer<? super E> part : parts) {
        part.accept(value);
      }
    };
  }

  /** Invoke ifTrue or ifFalse depending on what parameter. */
  public static <E> E either(boolean what, Supplier<E> ifTrue, Supplier<E> ifFalse) {
    return (what ? ifTrue : ifFalse).get();
  }

  /** Repeat runnable invocations until condition is true. */
  public static void repeat(Supplier<Boolean> condition, Runnable exec) {
    while (condition.get()) {
      exec.run();
    }
  }

  /** Collect elements returned by getter until condition is true; */
  public static <E> List<E> collectList(Supplier<Boolean> condition, Supplier<E> getter) {
    List<E> result = new ArrayList<>();
    repeat(condition, () -> result.add(getter.get()));
    return result;
  }

  /**
   * Creates stream consisting of elements returned by invocations of supply. Length of stream is
   * established by condition.
   */
  public static <T> Stream<T> stream(Supplier<Boolean> condition, Supplier<T> supply) {

    final Iterator<T> iterator =
        new Iterator<T>() {
          Boolean hasNext = null;

          @Override
          public boolean hasNext() {
            return hasNext == null ? hasNext = condition.get() : hasNext;
          }

          @Override
          public T next() {
            hasNext = null;
            return supply.get();
          }
        };

    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(iterator, ORDERED | IMMUTABLE), false);
  }
}
