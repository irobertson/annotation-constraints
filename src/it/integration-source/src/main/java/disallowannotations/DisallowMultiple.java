package disallowannotations;

import noconstraints.Unconstrained;
import requireconstructors.RequireNoArgConstructor;

import com.overstock.constraint.TargetDisallowsAnnotations;

@TargetDisallowsAnnotations({Unconstrained.class, RequireNoArgConstructor.class})
public @interface DisallowMultiple {
}
