package noconstraints;

import java.util.ArrayList;

@Unconstrained(someInt = 3)
public class NoConstraintsPass<T> extends ArrayList<T> {
  public static final long serialVersionUID = 1;
}
