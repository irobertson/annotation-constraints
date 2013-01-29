package com.overstock.constraint;

import java.lang.annotation.Inherited;

@Inherited
@RequireAnnotations(Unconstrained.class)
public @interface RequireUnconstrained {
}
