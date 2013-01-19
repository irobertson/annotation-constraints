package com.overstock.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requires that annotated types have specific constructors.
 */
@Constraint
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.ANNOTATION_TYPE)
public @interface RequireConstructors {

  /**
   * Required constructor signatures for classes annotated with the annotated annotation. If
   * {@code SomeAnnotation} is annotated by a {@code @RequireConstructors} annotation where
   * {@code value} contains {@code @RequiredConstructor(Foo.class, Bar.class)}, then
   * if the class {@code Baz} is annotated with {@code SomeAnnotation}, then there must be a constructor
   * with signature {@code Baz(Foo foo, Bar bar)}.
   */
  RequiredConstructor[] value();

}
