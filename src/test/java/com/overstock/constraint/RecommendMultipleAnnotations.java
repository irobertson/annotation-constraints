package com.overstock.constraint;

import java.lang.annotation.Inherited;

@Inherited
@RecommendAnnotations({Unconstrained.class, RequireNoArgConstructor.class})
public @interface RecommendMultipleAnnotations {
}
