package com.overstock.constraint.verifier;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import com.overstock.constraint.Constraint;
import com.overstock.constraint.processor.Constraints;
import com.overstock.constraint.provider.ProvidesConstraintsFor;

/**
 * Verifies that elements satisfy certain constraints presented by their annotations using the
 * {@link javax.lang.model Language Model API}.
 *
 * A Verifier is a <i>service</i> as
 * defined by {@link java.util.ServiceLoader}, and implementors of this interface are <i>service providers</i>.
 * Additional Verifier implementations can be listed, one fully-qualified binary class name per line, in a text file
 * named <tt>com.overstock.constraint.verifier.Verifier</tt> in the <tt>META-INF/services</tt> directory of any jar,
 * which is a <i>provider-configuration file</i> as specified by {@link java.util.ServiceLoader}. Verifier
 * implementations require a no-argument constructor just like any service provider.
 *
 * @see <a href="http://docs.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html">ServiceLoader API</a>
 * @see <a href="http://docs.oracle.com/javase/6/docs/api/javax/lang/model/package-summary.html">Language Model API</a>
 * @see <a href="http://docs.oracle.com/javase/6/docs/api/javax/annotation/processing/package-summary.html">Annotation Processing API</a>
 */
public interface Verifier {

  /**
   * Verify that an annotated element satisfies constraints imposed by an annotation which has been annotated with
   * one or more {@link Constraint} annotations or is externally constrained via {@link ProvidesConstraintsFor}. If the
   * element does not satisfy the constraints, use a {@link ProcessingEnvironment#getMessager() Messager} to report
   * warning or error messages on the element. Using
   * {@link Messager#printMessage(javax.tools.Diagnostic.Kind, CharSequence, Element, AnnotationMirror)} is encouraged
   * in order to provide helpful error messages.
   *
   * @param element the annotated element
   * @param constrained the constrained annotation, which is present on {@code element}
   * @param constraints the constraints on the annotation represented by {@code annotation}.
   */
  void verify(Element element, AnnotationMirror constrained, Constraints constraints); //TODO I don't like that the Messager is ultimately important here and yet not involved in this interface. Messager could be passed into verify or messages objects could be returned from verify.

  /**
   * Initializes the verifier. This method will be called exactly once for each verifier instance, and is guaranteed to
   * be called before {@link #verify(Element, AnnotationMirror, Constraints)}.
   *
   * @param environment environment to access facilities the annotation processing framework provides
   */
  void init(ProcessingEnvironment environment);

}
