package nestedclasses;

import requireconstructors.RequireNoArgConstructor;

import java.io.Serializable;

import com.overstock.constraint.TargetRequiresSupertypes;

public class Containing {

  @RequireNoArgConstructor
  public static class NestedClassPass {
  }

  @RequireNoArgConstructor
  public static class NestedClassFail {
    public NestedClassFail(String s) {}
  }

  @TargetRequiresSupertypes(Serializable.class)
  private static @interface NestedConstrained {}

  @NestedConstrained
  private static class NestedConstrainedPass implements Serializable {
    public static final long serialVersionUID = 1;
  }

  @NestedConstrained
  private static class NestedConstrainedFail {
  }

}