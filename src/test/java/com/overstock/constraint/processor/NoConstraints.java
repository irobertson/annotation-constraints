package com.overstock.constraint.processor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NoConstraints {
  int someInt() default 3;
}
