package com.overstock.constraint.provider;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.overstock.constraint.Constraint;

/**
 * Provides constraints on annotations in addition to any they already declare. This is useful for constraining
 * annotations for which you do not control the source code. See {@link ConstraintProvider} for details on
 * how to use this annotation.
 *
 * @see ConstraintProvider
 */
@Documented
@Constraint
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConstraintsFor {

  /**
   * The annotation for which to provide additional constraints.
   */
  Class<? extends Annotation> annotation();

  /**
   * The annotation which is annotated for constraints on behalf of {@link #annotation()}.
   */
  Class<? extends Annotation> canBeFoundOn();

}
