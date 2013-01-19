package com.overstock.constraint;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requires that annotated types NOT have specific annotations.
 */
@Constraint
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.ANNOTATION_TYPE)
public @interface DisallowAnnotations {

  /**
   * An array of annotations which cannot be present on any class annotated with the
   * annotated annotation.  If {@code @SomeAnnotation} is annotated with
   * {@code @DisallowAnnotations} where {@code value} is set to a
   * non-empty array of annotation classes, then it will be an error for any class annotated with
   * {@code @SomeAnnotation} which is also annotated by any annotation with a type from the
   * array.
   */
  Class<? extends Annotation>[] value();

}
