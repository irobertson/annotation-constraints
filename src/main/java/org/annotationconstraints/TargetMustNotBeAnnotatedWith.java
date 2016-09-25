package org.annotationconstraints;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.annotationconstraints.verifier.IncompatibleAnnotationsVerifier;

/**
 * Requires that annotated types NOT have specific annotations.
 */
@Constraint(verifiedBy = IncompatibleAnnotationsVerifier.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface TargetMustNotBeAnnotatedWith {

  /**
   * The annotations which cannot be present alongside the target annotation.
   *
   * For example, if {@code @SomeAnnotation} is annotated with {@code @TargetMustNotBeAnnotatedWith}, then it will be an error
   * for any class annotated with {@code @SomeAnnotation} to also be annotated with a type from this array.
   */
  Class<? extends Annotation>[] value();

}
