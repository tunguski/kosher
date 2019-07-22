package pl.matsuo.gitlab.util;

/** Created by marek on 07.07.15. */
@FunctionalInterface
public interface TriFunction<A, B, C, R> {
  R apply(A a, B b, C c);
}
