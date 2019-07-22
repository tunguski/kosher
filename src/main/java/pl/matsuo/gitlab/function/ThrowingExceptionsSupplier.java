package pl.matsuo.gitlab.function;

/** Created by marek on 01.04.14. */
public interface ThrowingExceptionsSupplier<E> {
  E get() throws Exception;
}
