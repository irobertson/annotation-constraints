package org.annotationconstraints;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes a constructor signature.
 *
 * @see TargetMustHaveConstructors
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({}) //cannot be annotated on anything directly
public @interface Constructor {

  /**
   * The types of the arguments of the required constructor, in order. An empty array represents a constructor with no
   * arguments.
   */
  Class<?>[] value();

}
