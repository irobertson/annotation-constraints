package requireannotationsonsupertype;

import companionannotations.RequireUnconstrained;
import noconstraints.Unconstrained;

import java.lang.annotation.Inherited;

import com.overstock.constraint.TargetRequiresAnnotationsOnSupertype;

@Inherited
@TargetRequiresAnnotationsOnSupertype({Unconstrained.class, RequireUnconstrained.class})
public @interface RequireUnconstrainedSupertype {
}
