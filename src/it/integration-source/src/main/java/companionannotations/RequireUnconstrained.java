package companionannotations;

import noconstraints.Unconstrained;

import java.lang.annotation.Inherited;

import com.overstock.constraint.TargetMustBeAnnotatedWith;

@Inherited
@TargetMustBeAnnotatedWith(Unconstrained.class)
public @interface RequireUnconstrained {
}
