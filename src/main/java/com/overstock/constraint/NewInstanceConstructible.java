package com.overstock.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/**
 * Marks a type and its subtypes as being able to be constructed via {@link Class#newInstance()}. This requires a
 * constructor with no arguments.
 */
@Inherited
@Documented
@TargetRequiresConstructors(@RequiredConstructor({}))
@Target(ElementType.TYPE)
public @interface NewInstanceConstructible {
}
