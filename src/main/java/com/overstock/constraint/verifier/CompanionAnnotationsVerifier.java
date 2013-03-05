package com.overstock.constraint.verifier;

import java.util.Collections;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import com.overstock.constraint.TargetMustBeAnnotatedWith;
import com.overstock.constraint.TargetShouldBeAnnotatedWith;
import com.overstock.constraint.processor.ConstraintMirror;
import com.overstock.constraint.processor.MirrorUtils;

/**
 * A verifier for {@link com.overstock.constraint.TargetMustBeAnnotatedWith} and {@link com.overstock.constraint.TargetShouldBeAnnotatedWith}.
 */
public class CompanionAnnotationsVerifier implements Verifier {

  @Override
  public void verify(VerificationContext context) {
    Types typeUtils = context.getTypeUtils();
    Elements elementUtils = context.getElementUtils();
    DeclaredType constraintType = context.getConstraint().getAnnotation().getAnnotationType();

    ConstraintMirror requireAnnotations = null;
    if (MirrorUtils.isSameType(TargetMustBeAnnotatedWith.class, constraintType, typeUtils, elementUtils)) {
      requireAnnotations = context.getConstraint();
    }

    ConstraintMirror recommendAnnotations = null;
    if (MirrorUtils.isSameType(TargetShouldBeAnnotatedWith.class, constraintType, typeUtils, elementUtils)) {
      recommendAnnotations = context.getConstraint();
    }

    final List<TypeMirror> requiredAnnotations = requireAnnotations == null ? Collections.<TypeMirror>emptyList()
      : VerifierUtils.getValuesAsTypes(requireAnnotations.getAnnotation());
    final List<TypeMirror> recommendedAnnotations = recommendAnnotations == null ? Collections.<TypeMirror>emptyList()
      : VerifierUtils.getValuesAsTypes(recommendAnnotations.getAnnotation());

    if (requiredAnnotations.isEmpty() && recommendedAnnotations.isEmpty()) {
      return; //no constraints
    }

    for (AnnotationMirror annotated : context.getElement().getAnnotationMirrors()) {
      TypeMirror annotatedType = annotated.getAnnotationType().asElement().asType();
      VerifierUtils.removeType(requiredAnnotations, annotatedType, typeUtils);
      VerifierUtils.removeType(recommendedAnnotations, annotatedType, typeUtils);
      if (requiredAnnotations.isEmpty() && recommendedAnnotations.isEmpty()) {
        return; //early termination
      }
    }

    if (!recommendedAnnotations.isEmpty()) {
      MessageBuilder.format(Diagnostic.Kind.WARNING, context)
        .appendText(" but not with ")
        .appendAnnotations(recommendedAnnotations, " and ")
        .print();
    }

    if (!requiredAnnotations.isEmpty()) {
      MessageBuilder.format(Diagnostic.Kind.ERROR, context)
        .appendText(" but not with ")
        .appendAnnotations(requiredAnnotations, " and ")
        .print();
    }
  }

  @Override
  public void init(ProcessingEnvironment environment) {
  }
}
