package disallowannotations;

import noconstraints.Unconstrained;
import requireconstructors.RequireNoArgConstructor;

import org.annotationconstraints.TargetMustNotBeAnnotatedWith;

@TargetMustNotBeAnnotatedWith({Unconstrained.class, RequireNoArgConstructor.class})
public @interface DisallowMultiple {
}
