package pl.matsuo.gitlab.function;


/**
 * Created by marek on 01.04.14.
 */
public interface ThrowingExceptionsBiFunction<D, E, F> {
  F apply(D first, E second) throws Exception;
}
