package org.annotationconstraints.verifier;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import org.annotationconstraints.Constraint;
import org.annotationconstraints.processor.ConstraintMirror;
import org.annotationconstraints.provider.ProvidesConstraintsFor;

/**
 * Verifies that elements satisfy certain constraints presented by their annotations using the
 * {@link javax.lang.model Language Model API}. A verifier is referenced from one or more {@link Constraint}
 * annotations.
 * <p>
 * Implementors may find it convenient to extend {@link AbstractVerifier}.
 *
 * @see <a href="http://docs.oracle.com/javase/6/docs/api/javax/lang/model/package-summary.html">Language Model API</a>
 * @see <a href="http://docs.oracle.com/javase/6/docs/api/javax/annotation/processing/package-summary.html">Annotation Processing API</a>
 */
public interface Verifier {

  /**
   * Initializes the verifier. This method will be called exactly once for each verifier instance, and is guaranteed to
   * be called before {@link #verify(Element, AnnotationMirror, ConstraintMirror)}.
   *
   * @param environment environment to access facilities the annotation processing framework provides
   */
  void init(ProcessingEnvironment environment);

  /**
   * Verify that an annotated element satisfies constraints imposed by an annotation which has been annotated with
   * one or more {@link Constraint} annotations or is externally constrained via {@link ProvidesConstraintsFor}. If the
   * element does not satisfy the constraints, use a {@link javax.annotation.processing.Messager Messager} to report
   * warning or error messages on the element. Using {@link MessageBuilder} is encouraged in order to provide consistent
   * and detailed messages.
   *
   * @param element the annotated element
   * @param annotationMirror the annotation on the annotated element
   * @param constraint the constraint annotation which is on the annotation mirrored by {@code annotationMirror}
   */
  void verify(Element element, AnnotationMirror annotationMirror, ConstraintMirror constraint);

}
