package pl.matsuo.gitlab.function;


/**
 * Created by marek on 01.04.14.
 */
public interface ThrowingExceptionsConsumer<E> {
  void accept(E value) throws Exception;
}
