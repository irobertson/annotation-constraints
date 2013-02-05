package com.overstock.constraint;

import java.lang.annotation.Inherited;

@Inherited
@TargetRequiresAnnotations(Unconstrained.class)
public @interface RequireUnconstrained {
}
