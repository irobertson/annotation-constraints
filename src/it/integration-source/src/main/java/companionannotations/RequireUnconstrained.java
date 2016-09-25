package companionannotations;

import noconstraints.Unconstrained;

import java.lang.annotation.Inherited;

import org.annotationconstraints.TargetMustBeAnnotatedWith;

@Inherited
@TargetMustBeAnnotatedWith(Unconstrained.class)
public @interface RequireUnconstrained {
}
