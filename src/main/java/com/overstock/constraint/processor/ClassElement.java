package com.overstock.constraint.processor;

import javax.lang.model.element.TypeElement;

/**
 * A type element and it's associated ClassValue.  Per spec, {@link Object#equals(Object)} and
 * {@link Object#hashCode()} are not reliable for Elements, so use this class with sets and maps.
 */
public class ClassElement implements Type {
  public final ClassValue classValue;
  public final TypeElement element;

  public ClassElement(TypeElement element) {
    this.element = element;
    this.classValue = new ClassValue(element);
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null
    && obj instanceof ClassElement
    && classValue.equals(((ClassElement)obj).classValue);
  }

  @Override public int hashCode() {  return classValue.hashCode(); }
  @Override public String toString() { return classValue.getQualifiedName(); }
}
