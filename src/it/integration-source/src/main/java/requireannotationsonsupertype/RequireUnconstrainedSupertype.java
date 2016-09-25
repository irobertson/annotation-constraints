package requireannotationsonsupertype;

import companionannotations.RequireUnconstrained;
import noconstraints.Unconstrained;

import java.lang.annotation.Inherited;

import org.annotationconstraints.TargetMustHaveASupertypeAnnotatedWith;

@Inherited
@TargetMustHaveASupertypeAnnotatedWith({Unconstrained.class, RequireUnconstrained.class})
public @interface RequireUnconstrainedSupertype {
}
