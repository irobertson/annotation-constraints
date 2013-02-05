package com.overstock.constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes a required constructor for use with {@link TargetRequiresConstructors}.
 *
 * @see TargetRequiresConstructors
 */
@Retention(RetentionPolicy.CLASS)
@Target({}) //cannot be annotated on anything directly
public @interface RequiredConstructor {

  /**
   * The types of the arguments of the required constructor. An empty array indicates no arguments.
   */
  Class<?>[] value();

}