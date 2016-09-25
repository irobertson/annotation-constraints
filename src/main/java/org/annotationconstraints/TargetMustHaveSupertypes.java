package org.annotationconstraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.annotationconstraints.verifier.RequireSupertypesVerifier;

/**
 * Requires that annotated types have specific supertypes.
 */
@Constraint(verifiedBy = RequireSupertypesVerifier.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface TargetMustHaveSupertypes {

  /**
   * The classes which must be supertypes of any class annotated with the target annotation.
   *
   * For example, if {@code @SomeAnnotation} is annotated with
   * {@code @TargetMustHaveSupertypes(Superclass.class, Superinterface.class)} then it will be an error if a class
   * annotated with {@code @SomeAnnotation} does not extend {@code Superclass} and implement {@code Superinterface}.
   */
  Class<?>[] value();

}
