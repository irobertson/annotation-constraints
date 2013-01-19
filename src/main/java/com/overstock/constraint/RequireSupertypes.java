package com.overstock.constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Requires that annotated types have specific supertypes.
 */
@Constraint
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.ANNOTATION_TYPE)
public @interface RequireSupertypes {

  /**
   * An array of classes which must be supertypes of any class annotated with the annotated
   * annotation.  If {@code @SomeAnnotation} is annotated by a {@code @RequireSupertypes}
   * annotation where {@code value} is set to a non-empty array of classes,
   * then it will be an error if a class annotated with {@code @SomeAnnotation}
   * does not extend or implement (as appropriate) each of the classes in the array.
   */
  Class<?>[] value();

}
