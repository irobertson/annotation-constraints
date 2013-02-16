package nestedclasses;

import requireconstructors.RequireNoArgConstructor;

import java.lang.String;

public class Containing {

  @RequireNoArgConstructor
  public static class NestedClassPass {
  }

  @RequireNoArgConstructor
  public static class NestedClassFail {
    public NestedClassFail(String s) {}
  }

}