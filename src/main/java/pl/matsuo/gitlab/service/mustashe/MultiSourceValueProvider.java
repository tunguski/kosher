package pl.matsuo.gitlab.service.mustashe;

import java.util.*;
import java.util.function.Function;

import static java.util.Arrays.*;

/**
 * Created by marek on 05.09.15.
 */
public class MultiSourceValueProvider<E> {

  private final List<E> sources;


  public MultiSourceValueProvider(E ... sources) {
    this.sources = asList(sources);
  }


  public MultiSourceValueProvider(List<E> sources) {
    this.sources = new ArrayList<>(sources);
  }


  public <F> F get(Function<E, F> getter) {
    for (E source : sources) {
      F apply = getter.apply(source);
      if (apply != null) {
        return apply;
      }
    }

    return null;
  }


  public MultiSourceValueProvider<E> sub(E source) {
    List<E> sources = new ArrayList<>();
    sources.add(source);
    sources.addAll(this.sources);
    return new MultiSourceValueProvider<>(sources);
  }
}

