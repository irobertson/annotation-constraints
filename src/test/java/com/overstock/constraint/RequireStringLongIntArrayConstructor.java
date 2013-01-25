package com.overstock.constraint;

@RequireConstructors(@RequiredConstructor({String.class, long.class, int[].class}))
public @interface RequireStringLongIntArrayConstructor {
}
