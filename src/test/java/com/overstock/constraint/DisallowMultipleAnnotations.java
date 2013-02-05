package com.overstock.constraint;

@TargetDisallowsAnnotations({Unconstrained.class, RequireNoArgConstructor.class})
public @interface DisallowMultipleAnnotations {
}
