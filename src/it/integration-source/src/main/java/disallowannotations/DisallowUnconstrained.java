package disallowannotations;

import noconstraints.Unconstrained;

import org.annotationconstraints.TargetMustNotBeAnnotatedWith;

@TargetMustNotBeAnnotatedWith(Unconstrained.class)
public @interface DisallowUnconstrained {
}
