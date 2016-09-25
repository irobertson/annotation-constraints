package companionannotations;

import noconstraints.Unconstrained;
import requireconstructors.RequireNoArgConstructor;

import java.lang.annotation.Inherited;

import org.annotationconstraints.TargetMustBeAnnotatedWith;

@Inherited
@TargetMustBeAnnotatedWith({Unconstrained.class, RequireNoArgConstructor.class})
public @interface RequireMultiple {
}
