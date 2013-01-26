package com.overstock.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an annotation as providing a constraint which should be checked at compile-time. Any such annotation must have
 * an associated {@link com.overstock.constraint.verifier.Verifier}.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Constraint {
}
