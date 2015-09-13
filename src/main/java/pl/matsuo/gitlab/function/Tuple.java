package pl.matsuo.gitlab.function;


/**
 * Created by marek on 23.04.14.
 */
public class Tuple<A, B> {


  public final A left;
  public final B right;


  public Tuple(A left, B right) {
    this.left = left;
    this.right = right;
  }


  public static final <A, B> Tuple<A, B> tuple(A left, B right) {
    return new Tuple<>(left, right);
  }
}

