package pl.matsuo.gitlab.util;

/**
 * Created by marek on 07.07.15.
 */
@FunctionalInterface
public interface TriConsumer<A,B,C> {
  void accept(A a, B b, C c);
}
