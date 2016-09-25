package org.annotationconstraints;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.annotationconstraints.verifier.CompanionAnnotationsVerifier;

/**
 * Recommends that annotated types have specific annotations, generating a compiler warning when this constraint is
 * violated.
 */
@Constraint(verifiedBy = CompanionAnnotationsVerifier.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface TargetShouldBeAnnotatedWith {

  /**
   * An array of annotations which SHOULD be present on any class annotated with the annotated
   * annotation. If {@code SomeAnnotation} is annotated with {@code TargetShouldBeAnnotatedWith}
   * where {@code value} is set to a array of annotation
   * classes which includes {@code SomeOtherAnnotation.class}, then a compiler warning will be
   * generated whenever a class annotated with {@code @SomeAnnotation} is not also annotated
   * with {@code @SomeOtherAnnotation}.
   */
  Class<? extends Annotation>[] value();
}
