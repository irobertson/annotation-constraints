package com.overstock.constraint;

import java.lang.annotation.Inherited;

@Inherited
@TargetRecommendsAnnotations({Unconstrained.class, RequireNoArgConstructor.class})
public @interface RecommendMultipleAnnotations {
}
