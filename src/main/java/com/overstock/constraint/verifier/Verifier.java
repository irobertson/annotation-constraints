package com.overstock.constraint.verifier;

import javax.annotation.processing.ProcessingEnvironment;

import com.overstock.constraint.Constraint;
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
   * element does not satisfy the constraints, use a {@link VerificationContext#getMessager() Messager} to report
   * warning or error messages on the element. Using
   * {@link MessageBuilder#format(javax.tools.Diagnostic.Kind, VerificationContext)} is
   * encouraged in order to provide consistent and detailed messages.
   *
   * @param context the context for verification, which includes information about the annotated element and constraint
   * being verified
   * @see MessageBuilder#format(javax.tools.Diagnostic.Kind, VerificationContext)
   */
  void verify(VerificationContext context);

  /**
   * Initializes the verifier. This method will be called exactly once for each verifier instance, and is guaranteed to
   * be called before {@link #verify(VerificationContext)}.
   *
   * @param environment environment to access facilities the annotation processing framework provides
   */
  void init(ProcessingEnvironment environment);

}
