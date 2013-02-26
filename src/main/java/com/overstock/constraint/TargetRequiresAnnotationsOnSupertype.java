package com.overstock.constraint;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.overstock.constraint.verifier.RequireAnnotationsOnSupertypeVerifier;

/**
 * Requires that annotated types have specific annotations amongst all supertypes.
 */
@Constraint(verifiedBy = RequireAnnotationsOnSupertypeVerifier.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface TargetRequiresAnnotationsOnSupertype {

  /**
   * An array of annotations which must be present on some supertype of any class annotated with the
   * target annotation. For this purpose, a class is considered to be its own supertype, so it would also be acceptable
   * if the annotated class itself was also annotated with the required annotation(s).
   *
   * For example, if {@code @SomeAnnotation} is annotated with
   * {@code @TargetRequiresAnnotationsOnSupertype(OtherAnnotation.class)}, then any class annotated with {code @SomeAnnotation}
   * will be required to be annotated with {@code @OtherAnnotation} or to implement some interface or extend some class
   * which is annotated with {@code @OtherAnnotation}.
   */
  Class<? extends Annotation>[] value();

}
