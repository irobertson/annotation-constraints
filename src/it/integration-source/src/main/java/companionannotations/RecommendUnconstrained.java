package companionannotations;

import noconstraints.Unconstrained;

import java.lang.annotation.Inherited;

import org.annotationconstraints.TargetShouldBeAnnotatedWith;

@Inherited
@TargetShouldBeAnnotatedWith(Unconstrained.class)
public @interface RecommendUnconstrained {
}
