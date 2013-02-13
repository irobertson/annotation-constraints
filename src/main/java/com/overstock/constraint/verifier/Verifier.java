package com.overstock.constraint.verifier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import com.overstock.constraint.Constraint;
import com.overstock.constraint.processor.Constraints;
import com.overstock.constraint.provider.ConstraintsFor;

/**
 * Verifies that elements satisfy certain constraints presented by their annotations. A Verifier is a <i>service</i> as
 * defined by {@link java.util.ServiceLoader}, and implementors of this interface are <i>service providers</i>.
 * Additional Verifier implementations can be listed, one fully-qualified binary class name per line, in a text file
 * named <tt>com.overstock.constraint.verifier.Verifier</tt> in the <tt>META-INF/services</tt> directory of any jar,
 * which is a <i>provider-configuration file</i> as specified by {@link java.util.ServiceLoader}. Verifier
 * implementations require a no-argument constructor.
 *
 * @see <a href="http://docs.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html">ServiceLoader documentation</a>
 */
public interface Verifier {

  /**
   * Verify that an annotated element satisfies contracts imposed by an annotation which has been annotated with
   * one or more {@link Constraint} annotations or is externally constrained via {@link ConstraintsFor}.
   *
   * @param element the annotated element
   * @param constrained the constrained annotation, which is present on {@code element}
   * @param constraints the constraints on the annotation represented by {@code annotation}.
   */
  void verify(Element element, AnnotationMirror constrained, Constraints constraints);

  /**
   * Initializes the verifier. This method will be called exactly once for each verifier instance, and is guaranteed to
   * be called before {@link #verify(Element, AnnotationMirror, Constraints)}.
   *
   * @param environment environment to access facilities the annotation processing framework provides
   */
  void init(ProcessingEnvironment environment);

}
