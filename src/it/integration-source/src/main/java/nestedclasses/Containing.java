package nestedclasses;

import requireconstructors.RequireNoArgConstructor;

import java.io.Serializable;

import com.overstock.constraint.TargetMustHaveSupertypes;

public class Containing {

  @RequireNoArgConstructor
  public static class NestedClassPass {
  }

  @RequireNoArgConstructor
  public static class NestedClassFail {
    public NestedClassFail(String s) {}
  }

  @TargetMustHaveSupertypes(Serializable.class)
  private static @interface NestedConstrained {}

  @NestedConstrained
  private static class NestedConstrainedPass implements Serializable {
    public static final long serialVersionUID = 1;
  }

  @NestedConstrained
  private static class NestedConstrainedFail {
  }

}