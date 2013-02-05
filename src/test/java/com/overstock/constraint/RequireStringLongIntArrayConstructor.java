package com.overstock.constraint;

@TargetRequiresConstructors(@RequiredConstructor({String.class, long.class, int[].class}))
public @interface RequireStringLongIntArrayConstructor {
}
