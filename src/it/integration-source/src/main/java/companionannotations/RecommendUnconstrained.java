package companionannotations;

import noconstraints.Unconstrained;

import java.lang.annotation.Inherited;

import com.overstock.constraint.TargetRecommendsAnnotations;

@Inherited
@TargetRecommendsAnnotations(Unconstrained.class)
public @interface RecommendUnconstrained {
}
