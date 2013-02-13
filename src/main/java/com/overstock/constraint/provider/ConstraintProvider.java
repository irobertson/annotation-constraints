package com.overstock.constraint.provider;

/**
 * A provider of constraints for annotations which may or may not already be constrained. Implementations of this
 * interface must be annotated with {@link ConstraintsFor}, which specifies which annotation
 * will receive the additional constraints and which annotation is providing constraints on its behalf.
 *
 * A ConstraintProvider is a <i>service</i> as defined by {@link java.util.ServiceLoader}, and implementors of this
 * interface are <i>service providers</i>.
 * ConstraintProvider implementations should be listed, one fully-qualified binary class name per line, in a
 * text file named <tt>com.overstock.constraint.provider.ConstraintProvider</tt> in the <tt>META-INF/services</tt>
 * directory of any jar, which is a <i>provider-configuration file</i> as specified by {@link java.util.ServiceLoader}.
 * ConstraintProvider implementations require a no-argument constructor.
 *
 * @see <a href="http://docs.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html">ServiceLoader documentation</a>
 * @see ConstraintsFor
 */
//FIXME README
public interface ConstraintProvider {
}
