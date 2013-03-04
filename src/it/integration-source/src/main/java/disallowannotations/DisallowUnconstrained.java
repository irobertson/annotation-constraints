package disallowannotations;

import noconstraints.Unconstrained;

import com.overstock.constraint.TargetCannotBeAnnotatedWith;

@TargetCannotBeAnnotatedWith(Unconstrained.class)
public @interface DisallowUnconstrained {
}
