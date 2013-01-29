package com.overstock.constraint;

@DisallowAnnotations({Unconstrained.class, RequireNoArgConstructor.class})
public @interface DisallowMultipleAnnotations {
}
