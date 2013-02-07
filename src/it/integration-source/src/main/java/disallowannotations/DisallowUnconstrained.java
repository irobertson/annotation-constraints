package disallowannotations;

import noconstraints.Unconstrained;

import com.overstock.constraint.TargetDisallowsAnnotations;

@TargetDisallowsAnnotations(Unconstrained.class)
public @interface DisallowUnconstrained {
}
