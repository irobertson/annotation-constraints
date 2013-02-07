package companionannotations;

import noconstraints.Unconstrained;

import java.lang.annotation.Inherited;

import com.overstock.constraint.TargetRequiresAnnotations;

@Inherited
@TargetRequiresAnnotations(Unconstrained.class)
public @interface RequireUnconstrained {
}
