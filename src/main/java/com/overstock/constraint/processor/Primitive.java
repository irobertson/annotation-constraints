package com.overstock.constraint.processor;

import javax.lang.model.type.TypeKind;

public class Primitive implements Type {
  private final TypeKind typeKind;

  public Primitive(TypeKind typeKind) {
    if (!typeKind.isPrimitive()) {
      throw new IllegalArgumentException("typeKind " + typeKind + " is not a primitive");
    }
    this.typeKind = typeKind;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Primitive
      && typeKind.equals(((Primitive) obj).typeKind);
  }

  @Override
  public int hashCode() {
    return typeKind.hashCode();
  }

  @Override
  public String toString() {
    switch (typeKind) {
      case BOOLEAN: return "boolean";
      case BYTE: return "byte";
      case CHAR: return "char";
      case SHORT: return "short";
      case INT: return "int";
      case LONG: return "long";
      case FLOAT: return "float";
      case DOUBLE: return "double";
      default: throw new IllegalStateException("non primitive type " + typeKind);
    }
  }
}
