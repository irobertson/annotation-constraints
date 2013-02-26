package com.overstock.constraint.verifier;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import com.overstock.constraint.Constraint;
import com.overstock.constraint.processor.ConstraintMirror;
import com.overstock.constraint.provider.ProvidesConstraintsFor;

/**
 * Verifies that elements satisfy certain constraints presented by their annotations using the
 * {@link javax.lang.model Language Model API}. A verifier is referenced from one or more {@link Constraint}
 * annotations.
 *
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
   * @param constraint the constraint on the annotation represented by {@code annotation} which to validate
   */
  void verify(Element element, AnnotationMirror constrained, ConstraintMirror constraint); //TODO I don't like that the Messager is ultimately important here and yet not involved in this interface. Messager could be passed into verify or messages objects could be returned from verify.

  /**
   * Initializes the verifier. This method will be called exactly once for each verifier instance, and is guaranteed to
   * be called before {@link #verify(javax.lang.model.element.Element, javax.lang.model.element.AnnotationMirror, com.overstock.constraint.processor.ConstraintMirror)}.
   *
   * @param environment environment to access facilities the annotation processing framework provides
   */
  void init(ProcessingEnvironment environment);

}
