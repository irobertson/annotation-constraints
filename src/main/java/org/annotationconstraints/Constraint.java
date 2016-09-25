package org.annotationconstraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.annotationconstraints.verifier.Verifier;

/**
 * Marks an annotation as providing a constraint which should be checked at compile-time. Any such annotation must have
 * a corresponding {@link org.annotationconstraints.verifier.Verifier} in order to be verified at compile-time. It is
 * recommended that such constraining annotations have a retention policy of {@link RetentionPolicy#RUNTIME} so that
 * they may be used with {@link org.annotationconstraints.provider.ProvidesConstraintsFor}.
 *
 * @see org.annotationconstraints.provider.ProvidesConstraintsFor
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Constraint {

  /**
   * The verifier for the constraint.
   */
  Class<? extends Verifier> verifiedBy();

}
