package com.overstock.constraint.verifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * A verifier for {@link com.overstock.constraint.TargetCannotBeAnnotatedWith}.
 */
public class IncompatibleAnnotationsVerifier implements Verifier {

  @Override
  public void verify(VerificationContext context) {
    Collection<TypeMirror> disallowedAnnotations = VerifierUtils.getValuesAsTypes(context.getConstraint()
      .getAnnotation());
    if (disallowedAnnotations.isEmpty()) {
      return;
    }

    Types typeUtils = context.getTypeUtils();
    List<TypeMirror> presentAndDisallowed = new ArrayList<TypeMirror>();
    for (AnnotationMirror annotated : context.getElement().getAnnotationMirrors()) {
      TypeMirror annotatedType = VerifierUtils.asType(annotated);
      for (TypeMirror disallowedAnnotation : disallowedAnnotations) {
        if (typeUtils.isSameType(annotatedType, disallowedAnnotation)) {
          presentAndDisallowed.add(annotatedType);
        }
      }
    }

    if (!presentAndDisallowed.isEmpty()) {
      MessageBuilder.format(Diagnostic.Kind.ERROR, context)
        .appendText(" which is not allowed with ")
        .appendAnnotations(presentAndDisallowed, " or ")
        .print();
    }
  }

  @Override
  public void init(ProcessingEnvironment environment) {
  }
}
