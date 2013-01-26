package com.overstock.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requires that annotated types have specific supertypes.
 */
@Documented
@Constraint
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.ANNOTATION_TYPE)
public @interface RequireSupertypes {

  /**
   * The classes which must be supertypes of any class annotated with the target annotation.
   *
   * For example, if {@code @SomeAnnotation} is annotated with
   * {@code @RequireAnnotationsOnSupertype(Superclass.class, Superinterface.class)} then it will be an error if a class
   * annotated with {@code @SomeAnnotation} does not extend {@code Superclass} and implement {@code Superinterface}.
   */
  Class<?>[] value();

}
