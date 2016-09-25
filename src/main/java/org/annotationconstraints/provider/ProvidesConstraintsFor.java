package org.annotationconstraints.provider;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *   Marks the target annotation as providing constraints on behalf of another annotation. This is useful for
 *   constraining annotations for which you do not control the source code. It is recommended that the target annotation
 *   have {@code @Target({})} so that it cannot be placed on an element since doing so would not have the intended
 *   effect.
 * </p>
 * <p>
 *   To use this annotation, create an annotation and annotate it with {@code @ProvidesConstraintsFor(...)}. Then create
 *   a plain text file named {@value ProvidesConstraintsFor#PROVIDERS_FILE} with a single line containing the
 *   fully-qualified name of your new annotation and make sure this file is included in your jar.
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ProvidesConstraintsFor {

  public static final String PROVIDERS_FILE = "META-INF/org.annotationconstraints.provider.constraint-providers";

  /**
   * The annotation for which to provide additional constraints.
   */
  Class<? extends Annotation> value();

}
