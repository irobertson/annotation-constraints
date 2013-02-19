package com.overstock.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an annotation as providing a constraint which should be checked at compile-time. Any such annotation must have
 * a corresponding {@link com.overstock.constraint.verifier.Verifier} in order to be verified at compile-time. It is
 * recommended that such constraining annotations have a retention policy of {@link RetentionPolicy#RUNTIME} so that
 * they may be used with {@link com.overstock.constraint.provider.ProvidesConstraintsFor}.
 *
 * @see com.overstock.constraint.provider.ProvidesConstraintsFor
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Constraint {
}
