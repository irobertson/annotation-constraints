package com.overstock.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requires that annotated types have specific constructors.
 */
@Documented
@Constraint
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.ANNOTATION_TYPE)
public @interface TargetRequiresConstructors {

  /**
   * Required constructor signatures for classes annotated with the target annotation.
   *
   * For example, if {@code SomeAnnotation} is annotated with {@code @TargetRequiresConstructors(String.class, int.class)},
   * then it will be an error if a class annotated with {@code SomeAnnotation} does not have a constructor with
   * signature {@code (String, int)}.
   */
  RequiredConstructor[] value();

}
