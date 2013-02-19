package com.overstock.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/**
 * Marks a type and its subtypes as being a <i>service provider</i> as defined by {@link java.util.ServiceLoader}.
 * Service providers must have a no-argument constructor.
 *
 * @see <a href="http://docs.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html">ServiceLoader</a>
 */
@Inherited
@Documented
@TargetRequiresConstructors(@RequiredConstructor({}))
@Target(ElementType.TYPE)
public @interface ServiceProvider {
}
