package pl.matsuo.gitlab.function;

import java.util.function.Supplier;

/**
 * Created by marek on 22.03.14.
 */
public class Failure<E> implements Try<E> {


  @Override
  public E get() {
    throw new RuntimeException("Cannot get value from failure");
  }


  @Override
  public Try<E> ifFailure(Supplier<Try<E>> supplier) {
    return supplier.get();
  }


  @Override
  public boolean isFailure() {
    return true;
  }
}

