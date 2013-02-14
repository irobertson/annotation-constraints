package com.overstock.constraint.processor;

import javax.lang.model.element.AnnotationMirror;

/**
 * A mirror for a constraint annotation.
 */
public final class ConstraintMirror {

  private final AnnotationMirror annotation;

  private final String provider;

  public ConstraintMirror(AnnotationMirror annotation) {
    this(annotation, null);
  }

  public ConstraintMirror(AnnotationMirror annotation, String provider) {
    this.annotation = annotation;
    this.provider = provider;
  }

  public AnnotationMirror getAnnotation() {
    return annotation;
  }

  public String getProvider() {
    return provider;
  }

  public boolean isProvided() {
    return getProvider() != null;
  }

  @Override
  public int hashCode() {
    int result = annotation.hashCode();
    result = 31 * result + (provider != null ? provider.hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ConstraintMirror that = (ConstraintMirror) o;

    if (!annotation.equals(that.annotation)) {
      return false;
    }
    if (provider != null ? !provider.equals(that.provider) : that.provider != null) {
      return false;
    }

    return true;
  }
}
