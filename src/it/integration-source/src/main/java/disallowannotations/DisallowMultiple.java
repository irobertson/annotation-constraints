package disallowannotations;

import noconstraints.Unconstrained;
import requireconstructors.RequireNoArgConstructor;

import com.overstock.constraint.TargetCannotBeAnnotatedWith;

@TargetCannotBeAnnotatedWith({Unconstrained.class, RequireNoArgConstructor.class})
public @interface DisallowMultiple {
}
