package companionannotations;

import noconstraints.Unconstrained;

import java.lang.annotation.Inherited;

import com.overstock.constraint.TargetShouldBeAnnotatedWith;

@Inherited
@TargetShouldBeAnnotatedWith(Unconstrained.class)
public @interface RecommendUnconstrained {
}
