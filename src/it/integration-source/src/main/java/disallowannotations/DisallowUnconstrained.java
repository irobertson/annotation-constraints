package disallowannotations;

import noconstraints.Unconstrained;

import com.overstock.constraint.TargetMustNotBeAnnotatedWith;

@TargetMustNotBeAnnotatedWith(Unconstrained.class)
public @interface DisallowUnconstrained {
}
