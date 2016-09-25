package org.annotationconstraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.annotationconstraints.verifier.RequireConstructorsVerifier;

/**
 * Requires that annotated types have specific constructors.
 */
@Constraint(verifiedBy = RequireConstructorsVerifier.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface TargetMustHaveConstructors {

  /**
   * Required constructor signatures for classes annotated with the target annotation.
   *
   * For example, if {@code SomeAnnotation} is annotated with {@code @TargetMustHaveConstructors(String.class, int.class)},
   * then it will be an error if a class annotated with {@code SomeAnnotation} does not have a constructor with
   * signature {@code (String, int)}.
   */
  Constructor[] value();

}
