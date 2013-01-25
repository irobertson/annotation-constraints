package com.overstock.constraint.processor;

import javax.lang.model.element.TypeElement;

class ClassValue {

  private final String className;

  public ClassValue(TypeElement element) {
    this(element.getQualifiedName().toString()); //TODO handle enclosing class
  }

  public ClassValue(String className) {
    this.className = className;
  }

  @Override
  public int hashCode() {
    return className.hashCode();
  }

  @Override
  public boolean equals(Object anObject) {
    return anObject != null && anObject.getClass() == getClass() && className.equals(((ClassValue) anObject).className);
  }

  @Override
  public String toString() {
    return className;
  }

  public String getQualifiedName() {
    return className;
  }

  public String getClassName() {
    return className;
  }
}
