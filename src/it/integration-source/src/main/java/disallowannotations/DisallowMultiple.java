package disallowannotations;

import noconstraints.Unconstrained;
import requireconstructors.RequireNoArgConstructor;

import com.overstock.constraint.TargetMustNotBeAnnotatedWith;

@TargetMustNotBeAnnotatedWith({Unconstrained.class, RequireNoArgConstructor.class})
public @interface DisallowMultiple {
}
