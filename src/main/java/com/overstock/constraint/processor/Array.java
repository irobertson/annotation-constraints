package com.overstock.constraint.processor;

public class Array implements Type {
  private final Type baseType;

  public Array(Type baseType) {
    this.baseType = baseType;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Array
      && baseType.equals(((Array) obj).baseType);
  }

  @Override
  public int hashCode() {
    return 31 * baseType.hashCode();
  }

  @Override
  public String toString() {
    return baseType + "[]";
  }
}
